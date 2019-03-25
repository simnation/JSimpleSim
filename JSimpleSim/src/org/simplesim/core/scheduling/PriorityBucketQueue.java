/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way.
 * 
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Rene Kuhlemann
 *
 * @param <E> event type
 */
public class PriorityBucketQueue<E> implements IEventQueue<E> {

	private final SortedMap<Time, Set<E>> map=new TreeMap<>(); // ConcurrentSkipListMap<>();

	private final Object cache[];

	private int size=0; // number of elements in queue

	private int index=0;

	public PriorityBucketQueue(int chacheSize) {
		cache=new Object[chacheSize];
	}

	public PriorityBucketQueue() {
		this(0);
	}

	@SuppressWarnings("unchecked")
	private Set<E> createEntry() {
		if (index<=0)
			return new HashSet<>();
		else return (Set<E>) cache[--index];
	}

	private void recycle(Set<E> item) {
		item.clear();
		if (index<cache.length) cache[index++]=item;
	}

	@Override
	public Time getTime(E event) {
		for (final Time time : map.keySet()) {
			final Set<E> set=map.get(time);
			if (set.contains(event)) return time;
		}
		return null;
	}

	@Override
	public Time dequeue(E event) {
		for (final Time time : map.keySet()) {
			final Set<E> set=map.get(time);
			if (set.contains(event)) {
				set.remove(event);
				if (set.isEmpty()) map.remove(time);
				size--;
				return time;
			}
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Time getMin() {
		if (map.isEmpty()) return null;
		return map.firstKey();
	}

	@Override
	public void enqueue(E event, Time time) {
		Set<E> set=map.get(time);
		if (set==null) map.put(time,set=createEntry());
		set.add(event);
		size++;
	}

	@Override
	public E dequeue() {
		if (map.isEmpty()) return null;
		final Set<E> set=map.get(map.firstKey());
		final Iterator<E> iterator=set.iterator();
		if (!iterator.hasNext()) return null;
		final E result=iterator.next();
		iterator.remove();
		if (set.isEmpty()) map.remove(map.firstKey());
		size--;
		return result;
	}

	@Override
	public List<E> dequeueAll(Time time) {
		if (map.isEmpty()) return Collections.emptyList();
		final Set<E> set=map.get(time);
		if (set==null) return Collections.emptyList();
		final List<E> result=new ArrayList<>(set);
		size-=set.size();
		recycle(set);
		return result;
	}

	@Override
	public List<E> dequeueAll() {
		return dequeueAll(getMin());
	}

	@Override
	public String toString() {
		final StringBuffer sb=new StringBuffer();
		for (final Time entry : map.keySet()) {
			sb.append('[');
			sb.append(entry.toString());
			sb.append('|');
			sb.append(map.get(entry).size());
			sb.append(" items]\n");
		}
		return sb.toString();
	}

}
