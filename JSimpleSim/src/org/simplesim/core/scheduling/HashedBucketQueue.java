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

import java.util.HashMap;
import java.util.List;

/**
 * Event queue based on a simple time to bucket mapping, each bucket containing
 * events with equal time stamps. The value of the most imminent time stamp is
 * saved to accelerate {@code getMin()} and has to be kept in sync.
 * <p>
 * Note: This queue is suitable for a global event queue, especially if there
 * are a lot of events with the same time stamp. This implementation has a
 * smaller memory footprint than the {@code SortedBucketQueue} and
 * {@code HeapBucketQueue}, but on all dequeue operation, the new minimal time
 * stamp has to be found with a complexity of O(n).
 *
 * @param <E> event type
 *
 * @see SortedBucketQueue
 * @see HeapBucketQueue
 */
public final class HashedBucketQueue<E> extends AbstractBucketQueue<E, HashMap<Time, List<E>>> {

	private Time minTime=Time.INFINITY;

	public HashedBucketQueue() {
		super(new HashMap<>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		if (time.getTicks()<minTime.getTicks()) minTime=time;
		super.enqueue(event,time);
	}

	@Override
	void removeEmptyBucket(Time time) {
		super.removeEmptyBucket(time);
		if (time.equals(minTime)) minTime=findNewMinTime();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> result=super.dequeueAll(time);
		if (time.equals(minTime)) minTime=findNewMinTime();
		return result;
	}

	private Time findNewMinTime() {
		Time result=Time.INFINITY;
		if (!getMap().isEmpty()) for (Time time : getMap().keySet()) {
			if (time.getTicks()<result.getTicks()) result=time;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getMin()
	 */
	@Override
	public Time getMin() { return minTime; }

}