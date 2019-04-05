/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Event queue with a sorted list of buckets, each bucket containing events with
 * equal time stamps.
 * </p>
 * This queue is suitable for a global event queue, especially if you have a lot
 * of events with the same time stamp. {@code getMin()}, {@code dequeue()} and
 * {@code dequeAll()} are faster than in a {@link HashedBucketQueue}, since this
 * queue is sorted, but for the price of a larger memory footprint.
 *
 * @author Rene Kuhlemann
 *
 * @param <E> event type
 */
public final class SortedBucketQueue<E> extends AbstractBucketQueue<E, SortedMap<Time, Set<E>>> {

	/**
	 * Constructor allowing to parameterize your own variant of a {@link SortedMap}.
	 * For example {@link TreeMap}, {@link ConcurrentSkipListMap} or another
	 * implementation from the Google Guava package.
	 *
	 * @param map {@code SortedMap} implementation to be used
	 */
	public SortedBucketQueue(SortedMap<Time, Set<E>> map) {
		super(map);
	}

	/**
	 * Constructor initializing a {@link TreeMap} as variant of a {@link SortedMap}.
	 * You may also use a {@link ConcurrentSkipListMap} or another implementation.
	 *
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

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		final Set<E> set=getMap().get(getMap().firstKey());
		final Iterator<E> iterator=set.iterator();
		if (!iterator.hasNext()) return null;
		final E result=iterator.next();
		iterator.remove();
		if (set.isEmpty()) getMap().remove(getMap().firstKey());
		size--;
		return result;
	}

}
