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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Event queue implementing a bucket strategy with a binary heap data structure.
 * <p>
 * Just as the other bucket queues, this one is also based on a simple time to
 * bucket mapping, each bucket containing events with equal time stamps. In this
 * implementation, an additional {@code PriorityQueue} is used to accelerate
 * access to the minimal time stamp by {@code getMin()}. Within this queue, an
 * {@code EventQueueEntry} couples the time stamp with the bucket. Because of
 * the underlying heap structure, the look-up of the first element is faster
 * than in a {@code HashedBucketQueue}.
 * <p>
 * Note: This queue type performs best for dequeuing <i>all</i> events
 * with minimal time stamp ({@link #dequeueAll()}). It is well suited for a
 * global event queue and has a performance similar to the
 * {@code SortedBucketQueue}.
 *
 * @param <E> event type
 *
 * @see HashedBucketQueue
 * @see SortedBucketQueue
 */
public final class HeapBucketQueue<E> extends AbstractBucketQueue<E, HashMap<Time, List<E>>> {

	// additional heap structure to facilitate getMin()
	private final Queue<EventQueueEntry<List<E>>> queue=new PriorityQueue<>();

	public HeapBucketQueue() {
		super(new HashMap<>());
	}

	private Queue<EventQueueEntry<List<E>>> getQueue() {
		return queue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue(java.lang.Object)
	 */
	@Override
	public Time dequeue(E event) {
		final Iterator<EventQueueEntry<List<E>>> iterator=getQueue().iterator();
		while (iterator.hasNext()) {
			final EventQueueEntry<List<E>> entry=iterator.next();
			final List<E> bucket=entry.getEvent();
			if (bucket.contains(event)) {
				bucket.remove(event);
				if (bucket.isEmpty()) {
					getMap().remove(entry.getTime());
					getQueue().remove(entry);
				}
				size--;
				return entry.getTime();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		final Time min=getMin();
		final List<E> bucket=getMap().get(min);
		if (bucket.isEmpty()) throw new UnexpectedEmptyBucketException();
		final E result=bucket.remove(bucket.size()-1);
		if (bucket.isEmpty()) {
			getMap().remove(min);
			getQueue().poll();
		}
		size--;
		return result;
	}

	@Override
	public void enqueue(E event, Time time) {
		List<E> bucket=getMap().get(time);
		if (bucket==null) { // time stamp has not been added to queue, yet
			bucket=new ArrayList<>();
			getMap().put(time,bucket);
			getQueue().add(new EventQueueEntry<>(time,bucket));
		} // now we definitely have a valid entry queued in the heap
		bucket.add(event); // add the event to the bucket
		size++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		final List<E> bucket=super.dequeueAll(time);
		if (!bucket.isEmpty()) { // remove bucket also from queue
			final Iterator<EventQueueEntry<List<E>>> iterator=getQueue().iterator();
			while (iterator.hasNext()) {
				final EventQueueEntry<List<E>> entry=iterator.next();
				if (time.equals(entry.getTime())) {
					iterator.remove();
					return bucket;
				}
			}
		}
		return bucket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		final List<E> bucket=getMap().remove(getMin());
		if (bucket.isEmpty()) throw new UnexpectedEmptyBucketException();
		getQueue().poll();
		size-=bucket.size();
		return bucket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		return getQueue().peek().getTime();
	}

}
