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
import java.util.Iterator;
import java.util.List;

/**
 * Event queue based on a simple time to bucket mapping, each bucket containing
 * events with equal time stamps.
 * <p>
 * Elements of this queue type are unsorted, so {@code getMin()} always has a complexity of O(n).
 * <p>
 * Note: This queue is suitable for a global event queue, especially if there are a lot
 * of events with the same time stamp. This implementation has a smaller memory
 * footprint than the {@code SortedBucketQueue} and {@code HeapBucketQueue}, but all time-based look up
 * operations (including {@code getMin()}, {@code dequeue()} and
 * {@code dequeAll()}) are slower.
 *
 * @param <E> event type
 * 
 * @see SortedBucketQueue
 * @see HeapBucketQueue
 */
public final class HashedBucketQueue<E> extends AbstractBucketQueue<E,HashMap<Time,List<E>>> {

	public HashedBucketQueue() {
		super(new HashMap<>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		final Iterator<Time> iterator=getMap().keySet().iterator();
		Time min=iterator.next();
		while (iterator.hasNext()) {
			final Time time=iterator.next();
			if (time.compareTo(min)<0) min=time;
		}
		return min;
	}

}