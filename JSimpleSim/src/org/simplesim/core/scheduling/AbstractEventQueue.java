/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.Collection;
import java.util.Iterator;

/**
 * This abstract class implements basic functionality to be extended by concrete
 * event queues. <br>
 * Any collection type can be passed as a container for the queue by subclasses.
 * There is also a basic cache functionality implemented to recycle unused
 * entries of the event queue {@link EventQueueEntry}.
 *
 * @author Rene Kuhlemann
 *
 * @param <E> event type
 */
public abstract class AbstractEventQueue<E, Q extends Collection<EventQueueEntry<E>>> implements IEventQueue<E> {

	private final Q queue; // the queue as subclass of a Collection
	
	private final ItemCache<EventQueueEntry<E>> cache;

	/**
	 * Class constructor taking the event queue and the cache size as parameters
	 *
	 * @param eq         the event queue, implementing a {@link Collection}
	 * @param chacheSize size of internal element cache, set to 0 to switch of
	 *                   caching.
	 */
	AbstractEventQueue(Q eq, int chacheSize) {
		queue=eq;
		cache=new ItemCache<>(chacheSize);
	}

	/**
	 * Gets the event queue.
	 *
	 * @return the event queue
	 */
	Q getQueue() {
		return queue;
	}
	
	/**
	 * Gets the cache.
	 *
	 * @return the cache
	 */
	ItemCache<EventQueueEntry<E>> getCache() {
		return cache;
	}

	/**
	 * Provides a new {@link EventQueueEntry} with given time and event type.
	 * If there are cached elements they are reused, otherwise the entry is created.
	 *
	 * @param event the event of the entry
	 * @param time  the time stamp of the event
	 *
	 * @return an {@code EventQueueEntry} with given event type and time stamp
	 */
	EventQueueEntry<E> createEntry(E event, Time time) {
		EventQueueEntry<E> entry=cache.reuse();
		if (entry==null) return new EventQueueEntry<>(event,time);
		entry.setEvent(event);
		entry.setTime(time);
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#getTime(java.lang.Object)
	 */
	@Override
	public Time getTime(E event) {
		for (final EventQueueEntry<E> entry : getQueue()) if (entry.getEvent().equals(event)) return entry.getTime();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#size()
	 */
	@Override
	public int size() {
		return queue.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		getQueue().add(createEntry(event,time));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
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
