/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
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
 * Time stamps are mapped to buckets, a bucket is represented internally by an
 * {@code ArrayList}.
 * <p>
 * Map-based queues usually differ in how they find the bucket with the least
 * time stamp, so the method {@code getMin()} is abstract and has to be
 * implemented by subclasses.
 *
 * @param <E> type of events
 * @param <M> type of map used for time to bucket mapping
 */
abstract class AbstractBucketQueue<E, M extends Map<Time, List<E>>> implements EventQueue<E> {

	/** the collection of buckets organized as a map: time --> bucket */
	private final M map;

	/** number of total events, accessible for subclasses */
	int size=0;

	@SuppressWarnings("serial")
	static class UnexpectedEmptyBucketException extends RuntimeException {

		public UnexpectedEmptyBucketException() {
			super("Bucket queue contains an empty bucket!");
		}
	}

	public AbstractBucketQueue(M m) {
		map=m;
	}

	M getMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getTime(java.lang.Object)
	 */
	@Override
	public Time getTime(E event) {
		for (final Time time : getMap().keySet()) {
			final List<E> bucket=getMap().get(time);
			if (bucket.contains(event)) return time;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue(java.lang.Object)
	 */
	@Override
	public Time dequeue(E event) {
		for (final Time time : getMap().keySet()) {
			final List<E> bucket=getMap().get(time);
			if (bucket.contains(event)) {
				bucket.remove(event);
				if (bucket.isEmpty()) getMap().remove(time);
				size--;
				return time;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		List<E> bucket=getMap().get(time);
		if (bucket==null) {
			bucket=new ArrayList<>();
			getMap().put(time,bucket);
		}
		bucket.add(event);
		size++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		final Time min=getMin(); // various getMin() implementations in derived queue classes
		final List<E> bucket=getMap().get(min);
		if (bucket.isEmpty()) throw new UnexpectedEmptyBucketException();
		final E result=bucket.remove(bucket.size()-1);
		if (bucket.isEmpty()) getMap().remove(min);
		size--;
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> bucket=getMap().remove(time);
		if (bucket==null) return Collections.emptyList();
		size-=bucket.size();
		return bucket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
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
