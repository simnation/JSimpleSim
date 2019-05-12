/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.core.scheduling;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Event queue with a sorted list of buckets, each bucket containing events with
 * equal time stamps.
 * </p>
 * This queue is suitable for a global event queue, especially if there are a lot
 * of events with the same time stamp. {@code getMin()}, {@code dequeue()} and
 * {@code dequeAll()} are faster than in a {@code HashedBucketQueue}, since this
 * queue is sorted, but for the price of a larger memory footprint.
 *
 * @param <E> event type
 * 
 * @see HashedBucketQueue
 * @see HeapBucketQueue
 */
public final class SortedBucketQueue<E> extends AbstractBucketQueue<E, SortedMap<Time, List<E>>> {

	/**
	 * Constructor allowing to parameterize your own variant of a {@code SortedMap}.
	 * For example {@link TreeMap}, {@link ConcurrentSkipListMap} or another
	 * implementation from the Google Guava package.
	 *
	 * @param map {@code SortedMap} implementation to be used
	 */
	public SortedBucketQueue(SortedMap<Time, List<E>> map) {
		super(map);
	}

	/**
	 * Default constructor using a {@code TreeMap} as implementation of a {@code SortedMap}.
	 */
	public SortedBucketQueue() {
		this(new TreeMap<>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		return getMap().firstKey();
	}

}
