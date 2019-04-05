/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Event queue based on a simple time to bucket mapping, each bucket containing
 * events with equal time stamps.
 * </p>
 * This queue is suitable for a global event queue, especially if you have a lot
 * of events with the same time stamp. This implementation has a smaller memory
 * footprint than the {@link SortedBucketQueue}, but all time-based look up
 * operations (including {@code getMin()}, {@code dequeue()} and
 * {@code dequeAll()}) are slower.
 *
 * @author Rene Kuhlemann
 *
 * @param <E> event type
 */
public final class HashedBucketQueue<E> extends AbstractBucketQueue<E, HashMap<Time, Set<E>>> {

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

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null; // should never happen!
		final Time min=getMin();
		final Set<E> set=getMap().get(min);
		if (set.isEmpty()) return null; // should never happen!
		final Iterator<E> iterator=set.iterator();
		final E result=iterator.next();
		iterator.remove();
		if (!iterator.hasNext()) getMap().remove(min); // only element dequeued, set is empty, remove from map
		size--;
		return result;
	}

}