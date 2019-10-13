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
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This implementation of the {@code IEventQueue} interface combines the characteristics of a binary
 * heap data structure with a bucket strategy.
 * <p>
 * Just as the other bucket queues, this one is also based on a simple time to bucket mapping, each 
 * bucket containing events with equal time stamps. In this implementation, an {@code EventQueueEntry}
 * is used to couple a time stamp with the bucket of corresponding events.
 * Because of the underlying heap structure, the look-up of the first element is faster than in a
 * {@code HashedBucketQueue} while still maintaining a small memory footprint.
 * <p>
 * So, well suited as global event queue, the {@code HeapBucketQueue} is situated between the {@code HashedBucketQueue}
 * and the {@code SortedBucketQueue} by its trade-off between memory usage and performance.
 * 
 * @param <E> event type
 * 
 * @see HashedBucketQueue
 * @see SortedBucketQueue
 */
public final class HeapBucketQueue<E> implements IEventQueue<E> {

	private final Queue<EventQueueEntry<List<E>>> queue=new PriorityQueue<>();

	private int size=0;

	public HeapBucketQueue() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeue(java.lang.Object)
	 */
	@Override
	public Time dequeue(E event) {
		for (final EventQueueEntry<List<E>> entry : queue) if (entry.getEvent().contains(event)) {
			entry.getEvent().remove(event);
			size--;
			if (entry.getEvent().isEmpty()) queue.remove(entry);
			return entry.getTime();
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
		final List<E> list=queue.peek().getEvent();
		if (list.isEmpty()) return null; // should never happen!
		final E result=list.remove(list.size()-1);
		if (list.isEmpty()) queue.poll(); // remove entry if bucket is empty
		size--;
		return result;
	}

	@Override
	public void enqueue(E event, Time time) {
		EventQueueEntry<List<E>> entry=null;
		for (final EventQueueEntry<List<E>> item : queue) if (time.equals(item.getTime())) {
			entry=item;
			break;
		}
		if (entry==null) { // means time stamp has not been added to list, yet
			entry=new EventQueueEntry<>(new ArrayList<>(),time);
			queue.add(entry);
		} // now we definitely have a valid entry queued in the heap
		entry.getEvent().add(event); // add the event to the bucket
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
		if (time.equals(getMin())) return dequeueAll();
		final Iterator<EventQueueEntry<List<E>>> iterator=queue.iterator();
		while (iterator.hasNext()) {
			final EventQueueEntry<List<E>> entry=iterator.next();
			if (entry.getTime().equals(time)) {
				iterator.remove();
				size-=entry.getEvent().size();
				return entry.getEvent();
			}
		}
		return Collections.emptyList(); // empty list if time stamp could not be found
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		final EventQueueEntry<List<E>> result=queue.poll();
		size-=result.getEvent().size();
		return result.getEvent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getTime(java.lang.Object)
	 */
	@Override
	public Time getTime(E event) {
		for (final EventQueueEntry<List<E>> entry : queue) if (entry.getEvent().contains(event)) return entry.getTime();
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.IEventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		return queue.peek().getTime();
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
		return size;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb=new StringBuffer();
		for (final EventQueueEntry<List<E>> entry : queue) {
			sb.append('[');
			sb.append(entry.getTime().toString());
			sb.append('|');
			sb.append(entry.getEvent().size());
			sb.append(" items]\n");
		}
		return sb.toString();
	}

}
