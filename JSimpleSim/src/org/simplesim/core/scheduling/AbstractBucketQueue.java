/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This abstract class implements basic functionality of a bucket queue to be
 * extended by concrete implementations.
 * </p>
 * Any {@link Map} can be passed as a container for the queue by subclasses.
 * Time stamps are mapped to buckets and a bucket is represented by a
 * {@link Set}. There is also a basic cache functionality implemented to recycle
 * unused sets of the event map.
 *
 * @author Rene Kuhlemann
 * @param <E> event type
 */
abstract class AbstractBucketQueue<E, M extends Map<Time, Set<E>>> implements IEventQueue<E> {

	private final M map;

	int size=0; // accessible by subclasses

	public AbstractBucketQueue(M m) {
		map=m;
	}

	M getMap() {
		return map;
	}

	@Override
	public Time getTime(E event) {
		for (final Time time : getMap().keySet()) {
			final Set<E> set=getMap().get(time);
			if (set.contains(event)) return time;
		}
		return null;
	}

	@Override
	public Time dequeue(E event) {
		for (final Time time : getMap().keySet()) {
			final Set<E> set=getMap().get(time);
			if (set.contains(event)) {
				set.remove(event);
				if (set.isEmpty()) getMap().remove(time);
				size--;
				return time;
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void enqueue(E event, Time time) {
		Set<E> set=getMap().get(time);
		if (set==null) {
			set=new HashSet<>();
			getMap().put(time,set);
		}
		set.add(event);
		size++;
	}

	@Override
	public List<E> dequeueAll(Time time) {
		if (getMap().isEmpty()) return Collections.emptyList();
		final Set<E> set=getMap().remove(time);
		if (set==null) return Collections.emptyList();
		final List<E> result=new ArrayList<>(set);
		size-=set.size();
		return result;
	}

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
