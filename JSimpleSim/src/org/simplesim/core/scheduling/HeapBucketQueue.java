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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Event queue implementing a bucket strategy with a binary heap data structure.
 * <p>
 * Just as the other bucket queues, this one is also based on a simple time to
 * bucket mapping, each bucket containing events with equal time stamps. In this
 * implementation, an additional {@code PriorityQueue} is used to accelerate
 * lookup of the minimal time stamp by {@code getMin()}. Thus, the queue has to
 * be maintained in parallel to the map and kept in sync.
 * <p>
 * Note: This queue type performs best for dequeuing <i>all</i> events with
 * minimal time stamp ({@link #dequeueAll()}). It is well suited for a global
 * event queue and has a performance similar to the {@code SortedBucketQueue}.
 *
 * @param <E> event type
 *
 * @see HashedBucketQueue
 * @see SortedBucketQueue
 */
public final class HeapBucketQueue<E> extends AbstractBucketQueue<E, HashMap<Time, List<E>>> {

	// additional heap structure to facilitate getMin()
	private final Queue<Time> queue=new PriorityQueue<>();

	public HeapBucketQueue() {
		super(new HashMap<>());
	}

	private Queue<Time> getQueue() { return queue; }

	@Override
	void removeEmptyBucket(Time time) {
		super.removeEmptyBucket(time);
		getQueue().remove(time);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		if (!getMap().containsKey(time)) getQueue().add(time);
		super.enqueue(event,time);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> bucket=super.dequeueAll(time);
		if (!bucket.isEmpty()) getQueue().remove(time);
		return bucket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		if (getQueue().isEmpty()) return Collections.emptyList();
		return super.dequeueAll(getQueue().poll());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getMin()
	 */
	@Override
	public Time getMin() { return getQueue().peek(); }

}
