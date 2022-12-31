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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * A multi-level queue is a layered event queue with three tiers suitable for a
 * large amount of events.
 * <p>
 * Both multi-level queue implementations are based on the article "MList: An
 * efficient pending event set structure for discrete event simulation" by Rick
 * Siow Mong Goh and Ian Li-Jin Thng. They are structured in three tiers:
 * <ol>
 * <li>current event tier for events before {@code minTimerTier2}
 * <li>near future tier for events since {@code minTimeTier2} but before
 * {@code minTimeTier3}
 * <li>far future tier for events from {@code minTimeTier3} to
 * {@code maxTimeTier3}
 * </ol>
 * {@code MultiLevelBucketQueue} uses buckets of events of the same time-stamp.
 * This is versatile if there are many event with the same time-stamp.
 * <p>
 * {@code MultiLevelEventQueue} uses {@code EventQueueEntry} to couple time and
 * events. This is versatile if there are many events with different time-stamps.
 *
 * @param <E> Event type
 * @see MultiLevelBucketQueue
 * @see <a href=
 *      "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.95.4263&rep=rep1&type=pdf">Referring
 *      article</a>
 */
public class MultiLevelEventQueue<E> implements EventQueue<E> {

	private final static int TIER2_DEFAULT_CHUNK_SIZE=128;

	private Queue<EventQueueEntry<E>> tier1=new PriorityQueue<>(); // current events, sorted
	private final List<Collection<EventQueueEntry<E>>> tier2=new ArrayList<>();// near future events, partly sorted
	private final Collection<EventQueueEntry<E>> tier3=new ArrayList<>(); // far future events, unsorted

	private int size=0;
	private int bucketWidth; // size of a bucket in tier 2 in time units
	private int chunkSizeTier2=TIER2_DEFAULT_CHUNK_SIZE;
	private int indexTier2=0; // actual index in array of tier 2
	private int maxIndexTier2=0; // maximum index in array of tier 2
	private long minTimeTier2=0; // minimum ticks in tier 2
	private long actTimeTier2=0;  // ticks of bucket at [indexTier2] in tier 2
	private long minTimeTier3=0; // minimum ticks in tier 3 (equals upper bound of tier 2)
	private long maxTimeTier3=0; // maximum ticks in tier 3

	/**
	 * Constructor to enable setting the average chunk size of the second sorting
	 * tier.
	 * <p>
	 * Only for optimization purpose, see documentation in {@code refillTier2}.
	 * Generally, use default constrcutor.
	 *
	 * @param chunkSize
	 */
	public MultiLevelEventQueue(int chunkSize) {
		chunkSizeTier2=chunkSize;
	}

	public MultiLevelEventQueue() {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#isEmptry()
	 */
	@Override
	public boolean isEmpty() { return (size==0); }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		if (tier1.isEmpty()) refillTier1();
		return tier1.peek().getTime();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(E,Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		final EventQueueEntry<E> entry=new EventQueueEntry<>(time,event);
		if (time.getTicks()>=minTimeTier3) {
			tier3.add(entry);
			if (time.getTicks()>maxTimeTier3) maxTimeTier3=time.getTicks();
		} else if (time.getTicks()<actTimeTier2) tier1.add(entry);
		else {
			final int index=(int) ((time.getTicks()-minTimeTier2)/bucketWidth);
			tier2.get(index).add(entry);
		}
		size++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue()
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
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		if (isEmpty()) return Collections.emptyList();
		
		final List<E> result=new ArrayList<>();
		final Time time=getMin(); // remember current time stamp, also refills tier1

		while (!tier1.isEmpty()&&tier1.peek().getTime().equals(time)) result.add(tier1.poll().getEvent());
		size-=result.size(); 
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) { // not yet implemented
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue(E)
	 */
	@Override
	public Time dequeue(E event) {
		throw new UnsupportedOperationException(); // to be implemented later
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getTime(E)
	 */
	@Override
	public Time getTime(E event) {
		throw new UnsupportedOperationException(); // to be implemented later
	}

	private void refillTier1() {
		if (indexTier2>=maxIndexTier2) refillTier2();
		tier1=new PriorityQueue<>(tier2.get(indexTier2)); // heapify() only called once when init with a collection.
		tier2.get(indexTier2).clear();
		indexTier2++;
		actTimeTier2+=bucketWidth;
	}

	private void refillTier2() {
		if (tier3.isEmpty()) throw new NoSuchElementException(); // empty queue should be tested before

		// adjust the number of buckets in tier2 depending on the number of elements in tier 3 as follows:
		// 1.) if there are less than TIER2_DEFAULT_BUCKET_SIZE items, ensure at least one bucket
		// 2.) if there are more than TIER2_DEFAULT_BUCKET_SIZE^2 items, use the square root as approximation
		// 3.) in all other cases use tier3.size/TIER2_DEFAULT_BUCKET_SIZE buckets
		int bucketCount=tier3.size()/chunkSizeTier2; 				// default: case 3.)
		if (tier3.size()<=chunkSizeTier2) bucketCount=1;			// case 1.)
		else if (tier3.size()>(chunkSizeTier2*chunkSizeTier2))		// case 2.)
			bucketCount=(int) Math.sqrt(tier3.size());
		// calc bucketWidth as time slice (deltaT) per bucket, rounded up to the next integer
		final long delta=maxTimeTier3-minTimeTier3;
		bucketWidth=(int) (delta/bucketCount)+1;
		// recalculate number of buckets, so that (maxIndexTier2 * bucketWidth) covers
		// the overall time span of tier3 as a convex hull.
		maxIndexTier2=(int) (delta/bucketWidth)+1;	// maxIndexTier2 replaces bucketCount
		indexTier2=0; 								// reset index
		actTimeTier2=minTimeTier2=minTimeTier3;		// transfer min time of tier3 to tier2
		minTimeTier3=maxTimeTier3;					// tier3 is now empty, so minTime equals maxTime

		// add additional lists to tier2 to match desired array size
		// existing maps are empty and will be reused
		while (tier2.size()<maxIndexTier2) tier2.add(new ArrayList<>());
		// transfer tier3 to tier2
		for (EventQueueEntry<E> entry : tier3) {
			final int index=(int) ((entry.getTime().getTicks()-minTimeTier2)/bucketWidth);
			tier2.get(index).add(entry);
		}
		tier3.clear();
	}

}
