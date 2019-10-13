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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This abstract class implements basic functionality of a bucket queue to be
 * extended by concrete implementations.
 * <p>
 * Any {@code Map} can be passed as a container for the queue by subclasses.
 * Time stamps are mapped to buckets, a bucket is represented by an
 * {@code ArrayList}.
 *
 * @param <E> type of events
 * @param <M> type of map containing the buckets
 */
abstract class AbstractBucketQueue<E, M extends Map<Time, List<E>>> implements IEventQueue<E> {

	/** the collection of buckets organized as a map: time --> bucket */
	private final M map;

	/** number of total events, accessible by subclasses */
	int size=0;

	public AbstractBucketQueue(M m) {
		map=m;
	}

	M getMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#getTime(java.lang.Object)
	 */
	@Override
	public Time getTime(E event) {
		for (final Time time : getMap().keySet()) {
			final List<E> list=getMap().get(time);
			if (list.contains(event)) return time;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue(java.lang.Object)
	 */
	@Override
	public Time dequeue(E event) {
		for (final Time time : getMap().keySet()) {
			final List<E> list=getMap().get(time);
			if (list.contains(event)) {
				list.remove(event);
				if (list.isEmpty()) getMap().remove(time);
				size--;
				return time;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		List<E> list=getMap().get(time);
		if (list==null) {
			list=new ArrayList<>();
			getMap().put(time,list);
		}
		list.add(event);
		size++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		final Time min=getMin(); // various getMin() implementations in derived queue classes
		final List<E> list=getMap().get(min);
		if (list.isEmpty()) return null; // should never happen!
		final E result=list.remove(list.size()-1);
		if (list.isEmpty()) getMap().remove(min); // only element dequeued, set is empty, remove from map
		size--;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> result=getMap().remove(time);
		if (result==null) return Collections.emptyList();
		size-=result.size();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		return dequeueAll(getMin());
	}

	@Override
	public String toString() {
		final StringBuffer sb=new StringBuffer();
		for (final Time entry : getMap().keySet()) {
			sb.append('[');
			sb.append(entry.toString());
			sb.append('|');
			sb.append(getMap().get(entry).size());
			sb.append(" items]\n");
		}
		return sb.toString();
	}

}
