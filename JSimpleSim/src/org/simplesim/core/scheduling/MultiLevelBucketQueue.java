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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.simplesim.core.scheduling.AbstractBucketQueue.UnexpectedEmptyBucketException;

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
 * events. This is versatile if there are many event with different time-stamps.
 *
 *
 * @param <E> Event type
 * @see <a href=
 *      "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.95.4263&rep=rep1&type=pdf">Referring
 *      article</a>
 */
public class MultiLevelBucketQueue<E> implements EventQueue<E> {

	private final static int TIER2_DEFAULT_CHUNK_SIZE=128;

	private final SortedMap<Time, List<E>> tier1=new TreeMap<>(); // current events, sorted
	private final List<Map<Time, List<E>>> tier2=new ArrayList<>();// near future events, partly sorted
	private final Map<Time, List<E>> tier3=new HashMap<>(); // far future events, unsorted

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
	public MultiLevelBucketQueue(int chunkSize) {
		chunkSizeTier2=chunkSize;
	}

	public MultiLevelBucketQueue() {}

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
		return tier1.firstKey();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(E,Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		if (time.getTicks()>=minTimeTier3) {
			addEventToTier(tier3,event,time);
			if (time.getTicks()>maxTimeTier3) maxTimeTier3=time.getTicks();
		} else if (time.getTicks()<actTimeTier2) addEventToTier(tier1,event,time);
		else {
			final int index=(int) ((time.getTicks()-minTimeTier2)/bucketWidth);
			addEventToTier(tier2.get(index),event,time);
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
		final List<E> bucket=tier1.get(getMin());
		if (bucket.isEmpty()) throw new UnexpectedEmptyBucketException();
		final E result=bucket.remove(bucket.size()-1);
		if (bucket.isEmpty()) tier1.remove(getMin());
		size--;
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		if (tier1.isEmpty()) refillTier1();
		final List<E> bucket=tier1.remove(getMin());
		if (bucket.isEmpty()) throw new UnexpectedEmptyBucketException();
		size-=bucket.size();
		return bucket;
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
		Time result=findAndRemoveEvent(tier1,event);
		if (result!=null) return result;
		result=findAndRemoveEvent(tier3,event);
		if (result!=null) return result;
		for (Map<Time, List<E>> map : tier2) {
			result=findAndRemoveEvent(map,event);
			if (result!=null) return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getTime(E)
	 */
	@Override
	public Time getTime(E event) {
		for (Map.Entry<Time, List<E>> entry : tier1.entrySet()) {
			if (entry.getValue().contains(event)) return entry.getKey();
		}
		for (Map<Time, List<E>> map : tier2) {
			for (Map.Entry<Time, List<E>> entry : map.entrySet()) {
				if (entry.getValue().contains(event)) return entry.getKey();
			}
		}
		for (Map.Entry<Time, List<E>> entry : tier3.entrySet()) {
			if (entry.getValue().contains(event)) return entry.getKey();
		}
		return null;
	}

	private void refillTier1() {
		if (indexTier2>=maxIndexTier2) refillTier2();
		tier1.putAll(tier2.get(indexTier2));
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
		while (tier2.size()<maxIndexTier2) tier2.add(new HashMap<>());
		// transfer tier3 to tier2
		for (Map.Entry<Time, List<E>> entry : tier3.entrySet()) {
			final int index=(int) ((entry.getKey().getTicks()-minTimeTier2)/bucketWidth);
			tier2.get(index).put(entry.getKey(),entry.getValue());
		}
		tier3.clear();
	}

	private void addEventToTier(Map<Time, List<E>> map, E event, Time time) {
		List<E> bucket=map.get(time);
		if (bucket==null) { // time stamp has not been added to queue, yet
			bucket=new ArrayList<>();
			map.put(time,bucket);
		} // now we definitely have a valid map entry
		bucket.add(event);
	}

	private Time findAndRemoveEvent(Map<Time, List<E>> map, E event) {
		for (Map.Entry<Time, List<E>> entry : map.entrySet()) {
			if (entry.getValue().contains(event)) {
				entry.getValue().remove(event);
				if (entry.getValue().isEmpty()) map.remove(entry.getKey());
				size--;
				return entry.getKey();
			}
		}
		return null;
	}

}
