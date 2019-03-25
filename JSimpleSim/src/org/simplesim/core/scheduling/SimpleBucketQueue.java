/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SimpleBucketQueue
 *
 * @author Rene Kuhlemann
 * @param <E> event type
 */
public class SimpleBucketQueue<E> implements IEventQueue<E> {

	private final Map<Time, Set<E>> queue=new HashMap<>();

	private final ItemCache<Set<E>> cache;

	private int size=0;

	public SimpleBucketQueue(int cacheSize) {
		cache=new ItemCache<>(cacheSize);
	}

	public SimpleBucketQueue() {
		this(0);
	}

	@Override
	public Time getTime(E event) {
		for (final Time time : queue.keySet()) {
			final Set<E> set=queue.get(time);
			for (final E element : set) if (element.equals(event)) return time;
		}
		return null;
	}

	@Override
	public Time dequeue(E event) {
		for (final Time time : queue.keySet()) {
			final Set<E> set=queue.get(time);
			for (final E element : set) if (element.equals(event)) {
				set.remove(element);
				if (set.isEmpty()) queue.remove(time);
				size--;
				return time;
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Time getMin() {
		Time min=null;
		final Iterator<Time> iterator=queue.keySet().iterator();
		while (iterator.hasNext()) {
			final Time time=iterator.next();
			if (time.compareTo(min)<0) min=time;
		}
		return min;
	}

	@Override
	public void enqueue(E event, Time time) {
		Set<E> set=queue.get(time);
		if (set==null) queue.put(time,set=cache.reuse());
		set.add(event);
		size++;
	}

	@Override
	public E dequeue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> dequeueAll(Time time) {
		final Set<E> set=queue.get(time);
		if (isEmpty()||(set==null)) return new ArrayList<>(0);
		final List<E> result=new ArrayList<>(set);
		size-=set.size();
		cache.recycle(set);
		return result;
	}

	@Override
	public List<E> dequeueAll() {
		return dequeueAll(getMin());
	}

	@Override
	public String toString() {
		final StringBuffer sb=new StringBuffer();
		for (final Time entry : queue.keySet()) {
			sb.append('[');
			sb.append(entry.toString());
			sb.append('|');
			sb.append(queue.get(entry).size());
			sb.append(" items]\n");
		}
		return sb.toString();
	}

}
