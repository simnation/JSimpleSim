/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * MList is a multi-layered event queue with three tiers
 * <p>
 * The MList queue is based on the article "MList: An efficient pending event
 * set structure for discrete event simulation" by Rick Siow Mong Goh and Ian
 * Li-Jin Thng. This implementation uses heap structures instead of linked
 * lists. The queue has three tiers:
 * <ol>
 * <li>current event tier for events before {@code minTimeTier2}
 * <li>near future tier for events since {@code minTimeTier2} but before
 * {@code minTimeTier3}
 * <li>far future tier for events from {@code minTimeTier3} to
 * {@code maxTimeTier3}
 * </ol>
 * The near future tier is sliced into equidistant time intervals. The far
 * future tier consists of an unsorted list. When shifting events form tier 3 to
 * tier 2, partial sorting is done by index calculation. A second sorting step
 * is done by the heap functionality of tier 1.
 *
 * @param <E> Event type
 *
 * @see <a href=
 *      "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.95.4263&rep=rep1&type=pdf">Referring
 *      article</a>
 *
 */
public class MListEventQueue<E> implements IEventQueue<E> {

	private final Queue<EventQueueEntry<E>> tier1=new PriorityQueue<>(); // current events
	private final List<List<EventQueueEntry<E>>> tier2=new ArrayList<>();// near future events
	private final List<EventQueueEntry<E>> tier3=new ArrayList<>(); // far future events

	private int size=0;
	private int bucketScale=7; // size of buckets in tier 2 is (1<<bucketScale), initially 128
	private int bucketWidth; // size of a bucket in tier 2 in time units
	private int indexTier2=0; // actual index in array of tier 2
	private int maxIndexTier2=0; // maximum index in array of tier 2
	private Time minTimeTier2=Time.ZERO; // time of the element indexTier2
	private Time minTimeTier3=Time.ZERO; // minimum time in tier 3
	private Time maxTimeTier3=Time.ZERO; // maximum time in tier 3

	/**
	 * Construct an MList with an average bucketSize in tier 2 of {@code (1<<scale)}
	 *
	 * @param scale scaling factor to calculate the bucketSize in tier 2, has to be
	 *              between 3 and 10
	 */
	public MListEventQueue(int scale) {
		bucketScale=scale; // ensure an average bucketSize of 8 to 1024
		if (bucketScale<2) bucketScale=3;
		else if (bucketScale>10) bucketScale=10;
	}

	public MListEventQueue() {
		this(7); // default is a bucketSize of 128
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#isEmptry()
	 */
	@Override
	public boolean isEmpty() {
		return (size==0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		if (tier1.isEmpty()) refillTier1();
		return tier1.peek().getTime();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#enqueue(E,Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		final EventQueueEntry<E> entry=new EventQueueEntry<>(time,event);
		if (time.getTicks()<minTimeTier3.getTicks()) { // is event in tier 1 or 2 ?
			if (time.getTicks()<getMinTicksTier2()) tier1.add(entry);
			else enqueueTier2(entry);
		} else {
			tier3.add(entry); // adjust maxTime of tier 3 if necessary
			if (time.getTicks()>maxTimeTier3.getTicks()) maxTimeTier3=time;
		}
		size++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		if (tier1.isEmpty()) refillTier1();
		size--;
		return tier1.poll().getEvent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		final List<E> result=new ArrayList<>();
		final Time time=getMin(); // remember current time stamp
		while ((!isEmpty())&&time.equals(getMin())) result.add(dequeue());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll(Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue(E)
	 */
	@Override
	public Time dequeue(E event) {
		Iterator<EventQueueEntry<E>> iter=tier1.iterator();
		while (iter.hasNext()) {
			final EventQueueEntry<E> entry=iter.next();
			if (entry.getEvent().equals(event)) {
				iter.remove();
				size--;
				return entry.getTime();
			}
		}
		for (final List<EventQueueEntry<E>> queue : tier2) {
			iter=queue.iterator();
			while (iter.hasNext()) {
				final EventQueueEntry<E> entry=iter.next();
				if (entry.getEvent().equals(event)) {
					iter.remove();
					size--;
					return entry.getTime();
				}
			}
		}
		iter=tier3.iterator();
		while (iter.hasNext()) {
			final EventQueueEntry<E> entry=iter.next();
			if (entry.getEvent().equals(event)) {
				iter.remove();
				size--;
				return entry.getTime();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getTime(E)
	 */
	@Override
	public Time getTime(E event) {
		for (final EventQueueEntry<E> entry : tier1) {
			if (entry.getEvent().equals(event)) return entry.getTime();
		}
		for (final List<EventQueueEntry<E>> list : tier2) {
			for (final EventQueueEntry<E> entry : list) {
				if (entry.getEvent().equals(event)) return entry.getTime();
			}
		}
		for (final EventQueueEntry<E> entry : tier3) {
			if (entry.getEvent().equals(event)) return entry.getTime();
		}
		return null;
	}

	private void refillTier2() {
		if (tier3.isEmpty()) throw new NoSuchElementException();

		// approximate number of buckets in tier2
		int approxBucketCount=tier3.size()>>bucketScale; // number of buckets based on an average size of (1<<bucketScale)
		if (approxBucketCount<2) approxBucketCount=2; // at least 2 buckets in tier2

		// calc bucketWidth as time slice per bucket, rounded up to next int
		bucketWidth=(int) ((maxTimeTier3.getTicks()-minTimeTier3.getTicks())/approxBucketCount)+1;
		// recalculate number of buckets, so that maxIndexTier2 * bucketWidth covers
		// the overall time span in tier3.
		maxIndexTier2=(int) ((maxTimeTier3.getTicks()-minTimeTier3.getTicks())/bucketWidth)+1;

		indexTier2=0; // reset index
		minTimeTier2=minTimeTier3; // transfer min time of tier3 to tier2
		minTimeTier3=maxTimeTier3; // tier3 is empty, so minTime equals maxTime

		// add additional lists to tier2 to match desired array size
		// existing lists are empty and will be reused
		while (tier2.size()<maxIndexTier2) tier2.add(new ArrayList<>());

		// transfer tier3 to tier2
		for (final EventQueueEntry<E> entry : tier3) enqueueTier2(entry);
		tier3.clear();
	}

	private void refillTier1() {
		if (indexTier2>=maxIndexTier2) refillTier2();
		final List<EventQueueEntry<E>> list=tier2.get(indexTier2);
		tier1.addAll(list);
		list.clear();
		indexTier2++;
	}

	private void enqueueTier2(EventQueueEntry<E> entry) {
		final int index=(int) ((entry.getTime().getTicks()-minTimeTier2.getTicks())/bucketWidth);
		tier2.get(index).add(entry);
	}
	
	private long getMinTicksTier2() {
		return minTimeTier2.getTicks()+indexTier2*bucketWidth;
	}

}
