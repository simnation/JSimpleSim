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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Provides basic functionality of an event queue to be extended by concrete
 * implementation in derived classes.<p>
 * Any collection type can be passed as a container for the queue by subclasses. 
 * Please refer to {@link AbstractBucketQueue} and its subclasses for the implementation 
 * of a bucket strategy.
 * 
 * @param <E> type of events
 * @param <Q> type of collection holding time-event pairs
 * 
 * @see EventQueueEntry
 * 
 */
abstract class AbstractEventQueue<E, Q extends Collection<EventQueueEntry<E>>> implements EventQueue<E> {

	private final Q queue; // the queue as subclass of a Collection
	
	/**
	 * Class constructor taking the event queue and the cache size as parameters
	 *
	 * @param eq         the event queue, implementing a {@link Collection}
	 * 
	 */
	AbstractEventQueue(Q q) {
		queue=q;
	}

	Q getQueue() {
		return queue;
	}

	@Override
	public Time getTime(E event) {
		for (final EventQueueEntry<E> entry : getQueue()) if (entry.getEvent().equals(event)) return entry.getTime();
		return null;
	}

	@Override
	public boolean isEmpty() {
		return getQueue().isEmpty();
	}

	@Override
	public int size() {
		return getQueue().size();
	}

	@Override
	public Time dequeue(E event) {
		final Iterator<EventQueueEntry<E>> iterator=getQueue().iterator();
		while (iterator.hasNext()) {
			final EventQueueEntry<E> entry=iterator.next();
			if (entry.getEvent().equals(event)) {
				iterator.remove();
				return entry.getTime();
			}
		}
		return null;
	}

	@Override
	public void enqueue(E event, Time time) {
		getQueue().add(new EventQueueEntry<E>(time,event));
	}
	
	@Override
	public List<E> dequeueAll(Time time) {
		if (time.equals(getMin())) return dequeueAll();
		final List<E> result=new ArrayList<>();
		final Iterator<EventQueueEntry<E>> iterator=getQueue().iterator();
		while (iterator.hasNext()) {
			final EventQueueEntry<E> entry=iterator.next();
			if (entry.getTime().equals(time)) {
				// copy all event queue entries with given time stamp to result list
				result.add(entry.getEvent());
				iterator.remove();
			}
		}
		return result;
	}

	@Override
	public List<E> dequeueAll() {
		final List<E> result=new ArrayList<>();
		final Time time=getMin(); // remember current time stamp
		while (!isEmpty()&&time.equals(getMin())) result.add(dequeue());
		return result;
	}

	@Override
	public String toString() {
		final StringBuffer sb=new StringBuffer();
		for (final EventQueueEntry<?> entry : getQueue()) {
			sb.append('[');
			sb.append(entry.getTime().toString());
			sb.append('|');
			sb.append(entry.getEvent().toString());
			sb.append("]\n");
		}
		return sb.toString();
	}

}