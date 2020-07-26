/*
 * https://store.steampowered.com/app/1285080/Gordian_Rooms_A_curious_heritage/?snr=1_4_4__135_10&curator_clanid=
 * 34454724 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published
 * as open source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The MultiLevelQueue is a layered event queue with three tiers suitable for a huge amount of events.
 * <p>
 * MultiLevelQueue is based on the article "MList: An efficient pending event set structure for discrete event
 * simulation" by Rick Siow Mong Goh and Ian Li-Jin Thng. The queue has three tiers:
 * <ol>
 * <li>current event tier for events before {@code minTimeTier2}
 * <li>near future tier for events since {@code minTimeTier2} but before {@code minTimeTier3}
 * <li>far future tier for events from {@code minTimeTier3} to {@code maxTimeTier3}
 * </ol>
 * The near future tier is sliced into equidistant time intervals. The far future tier consists of an unsorted list.
 * When shifting events form tier 3 to tier 2, partial sorting is done by index calculation. A second sorting step is
 * done by the heap functionality of tier 1.
 *
 * @param <E> Event type
 * @see <a href= "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.95.4263&rep=rep1&type=pdf">Referring
 *      article</a>
 */
public class MultiLevelQueue<E> implements EventQueue<E> {

	private final static int TIER2_DEFAULT_BUCKET_SIZE=128;

	private List<EventQueueEntry<E>> tier1=Collections.emptyList(); // current events, sorted descending
	private final List<List<EventQueueEntry<E>>> tier2=new ArrayList<>();// near future events
	private final List<EventQueueEntry<E>> tier3=new ArrayList<>(); // far future events

	private int size=0;
	private int bucketWidth; // size of a bucket in tier 2 in time units
	private int indexTier2=0; // actual index in array of tier 2
	private int maxIndexTier2=0; // maximum index in array of tier 2
	private Time minTimeTier2=Time.ZERO; // time of the element indexTier2
	private Time minTimeTier3=Time.ZERO; // minimum time in tier 3
	private Time maxTimeTier3=Time.ZERO; // maximum time in tier 3

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#isEmptry()
	 */
	@Override
	public boolean isEmpty() {
		return (size==0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		if (tier1.isEmpty()) refillTier1();
		return tier1.get(tier1.size()-1).getTime();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(E,Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		final EventQueueEntry<E> entry=new EventQueueEntry<>(time,event);		
		if (time.compareTo(minTimeTier3)<0) { // is event in tier 1 or 2 ?
			if (time.compareTo(getMinTicksTier2())<0) {
				tier1.add(entry);	// event in tier1: add and sort again to maintain descending order
				tier1.sort((o1, o2) -> -o1.compareTo(o2));
			} else enqueueTier2(entry);
		} else {
			tier3.add(entry); // add to tier3, adjust maxTime if necessary
			if (time.compareTo(maxTimeTier3)>0) maxTimeTier3=time;
		}
		size++;
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		if (tier1.isEmpty()) refillTier1();
		size--;
		return tier1.remove(tier1.size()-1).getEvent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		final List<E> result=new ArrayList<>();
		final Time time=getMin(); // remember current time stamp
		for (int index=tier1.size()-1; index>=0; index--) {
			if (!tier1.get(index).getTime().equals(time)) break;
			// remove all events with least time stamp
			// events with the same time stamp are always in the same queue
			result.add(tier1.remove(index).getEvent());
		}
		size-=result.size();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) { // not yet implemented
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue(E)
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
	 * @see org.simplesim.core.scheduling.EventQueue#getTime(E)
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
		if (tier3.isEmpty()) throw new NoSuchElementException(); // empty queue should be tested before

		// adjust the number of buckets in tier2 depending on the number of elements in tier 3 as follows:
		// 1.) if there are less than TIER2_DEFAULT_BUCKET_SIZE items, ensure at least one bucket
		// 2.) if there are more than TIER2_DEFAULT_BUCKET_SIZE^2 items, use the square root as approximation
		// 3.) in all other case use tier3.size/TIER2_DEFAULT_BUCKET_SIZE buckets
		int approxBucketCount=tier3.size()/TIER2_DEFAULT_BUCKET_SIZE; 
		if (tier3.size()<=TIER2_DEFAULT_BUCKET_SIZE) approxBucketCount=1;
		else if (approxBucketCount>TIER2_DEFAULT_BUCKET_SIZE) approxBucketCount=(int) Math.floor(Math.sqrt(tier3.size()));
		// calc bucketWidth as time slice per bucket, rounded up to the next int
		bucketWidth=(int) ((maxTimeTier3.getTicks()-minTimeTier3.getTicks())/approxBucketCount)+1;
		// recalculate number of buckets, so that maxIndexTier2 * bucketWidth covers
		// the overall time span of tier3. approxBucketCount is replace by maxIndexTier2 from here on.
		maxIndexTier2=(int) ((maxTimeTier3.getTicks()-minTimeTier3.getTicks())/bucketWidth)+1;
		indexTier2=0; // reset index
		minTimeTier2=minTimeTier3; // transfer min time of tier3 to tier2
		minTimeTier3=maxTimeTier3; // tier3 is empty, so minTime equals maxTime

		// add additional lists to tier2 to match desired array size
		// existing lists are empty and will be reused
		while (tier2.size()<maxIndexTier2) tier2.add(new ArrayList<>());

		// transfer tier3 to tier2
		for (EventQueueEntry<E> entry : tier3) enqueueTier2(entry);
		tier3.clear();
	}

	private void refillTier1() {
		if (indexTier2>=maxIndexTier2) refillTier2();
		tier1=tier2.get(indexTier2);
		tier1.sort((o1, o2) -> -o1.compareTo(o2));
		indexTier2++;
	}

	private void enqueueTier2(EventQueueEntry<E> entry) {
		final int index=(int) ((entry.getTime().getTicks()-minTimeTier2.getTicks())/bucketWidth);
		tier2.get(index).add(entry);
	}

	private Time getMinTicksTier2() {
		return minTimeTier2.add(indexTier2*bucketWidth);
	}

}
