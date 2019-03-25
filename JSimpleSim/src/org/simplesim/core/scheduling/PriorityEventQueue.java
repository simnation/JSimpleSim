/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The Class PriorityEventQueue. <br>
 * Adapter class wrapping a {@link PriorityQueue}
 *
 * @author Rene Kuhlemann
 * @param <E> event type
 */
public class PriorityEventQueue<E> extends AbstractEventQueue<E, PriorityQueue<EventQueueEntry<E>>> {

	public PriorityEventQueue(int cacheSize) {
		super(new PriorityQueue<EventQueueEntry<E>>(),cacheSize);
	}

	public PriorityEventQueue() {
		this(0);
	}

	@Override
	public Time getMin() {
		if (getQueue().isEmpty()) return null;
		return getQueue().peek().getTime();
	}

	@Override
	public E dequeue() {
		return getQueue().poll().getEvent();
	}

	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> result=new ArrayList<>();
		if (isEmpty()) return result;
		EventQueueEntry<E> entry=null;
		final Iterator<EventQueueEntry<E>> iterator=getQueue().iterator();
		while (iterator.hasNext()) {
			entry=iterator.next();
			if (entry.getTime().equals(time)) {
				do { // copy all event queue entries with given time stamp to result list
					result.add(entry.getEvent());
					iterator.remove();
					getCache().recycle(entry);
				} while (iterator.hasNext()&&(entry=iterator.next()).getTime().equals(time));
				return result;
			}
		}
		return result;
	}

	@Override
	public List<E> dequeueAll() {
		return dequeueAll(getMin());
	}

}
