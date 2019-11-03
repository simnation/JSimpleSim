/*
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
 * Event queue backed by a {@code TreeMap} with each bucket containing events of
 * equal time stamps
 * </p>
 * Note: This queue type performs best for dequeuing <i>all</i> events
 * with minimal time stamp ({@link #dequeueAll()}). It is well suited for a
 * global event queue and has a performance similar to the {@code HeapBucketQueue}.
 * <p>
 * @param <E> event type
 * 
 * @see HashedBucketQueue
 * @see HeapBucketQueue
 * @see TreeMap
 */
public final class SortedBucketQueue<E> extends AbstractBucketQueue<E,SortedMap<Time, List<E>>> {

	/**
	 * Constructor allowing to parameterize your own variant of a {@code SortedMap}.
	 * For example {@link TreeMap}, {@link ConcurrentSkipListMap} or any another
	 * implementation.
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
