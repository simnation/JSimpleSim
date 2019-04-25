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
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Heap bucket queue
 *
 * Note: The EventQueueEntry in this implementation couples a time stamps with a
 * Set of events!
 *
 * @author Rene Kuhlemann
 * @param <E> event type
 */
public final class HeapBucketQueue<E> implements IEventQueue<E> {

	private final Queue<EventQueueEntry<Set<E>>> queue=new PriorityQueue<>();

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
		for (final EventQueueEntry<Set<E>> entry : queue) if (entry.getEvent().contains(event)) {
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
		final EventQueueEntry<Set<E>> entry=queue.peek();
		final Iterator<E> iterator=entry.getEvent().iterator();
		if (!iterator.hasNext()) return null;
		final E result=iterator.next();
		iterator.remove();
		if (entry.getEvent().isEmpty()) queue.poll(); // remove entry if bucket is empty
		size--;
		return result;
	}

	@Override
	public void enqueue(E event, Time time) {
		EventQueueEntry<Set<E>> entry=null;
		for (final EventQueueEntry<Set<E>> item : queue) if (time.equals(item.getTime())) {
			entry=item;
			break;
		}
		if (entry==null) {
			entry=new EventQueueEntry<>(new HashSet<>(),time);
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
		if (isEmpty()) return Collections.emptyList();
		final List<E> result=new ArrayList<>();
		final Iterator<EventQueueEntry<Set<E>>> iterator=queue.iterator();
		while (iterator.hasNext()) {
			final EventQueueEntry<Set<E>> entry=iterator.next();
			if (entry.getTime().equals(time)) {
				result.addAll(entry.getEvent());
				size-=result.size();
				iterator.remove();
				entry.getEvent().clear();
				return result;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		final EventQueueEntry<Set<E>> entry=queue.poll();
		if (entry==null) return Collections.emptyList();
		final List<E> result=new ArrayList<>(entry.getEvent());
		size-=result.size();
		entry.getEvent().clear();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simplesim.core.scheduling.IEventQueue#getTime(java.lang.Object)
	 */
	@Override
	public Time getTime(E event) {
		for (final EventQueueEntry<Set<E>> entry : queue) if (entry.getEvent().contains(event)) return entry.getTime();
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
		for (final EventQueueEntry<Set<E>> entry : queue) {
			sb.append('[');
			sb.append(entry.getTime().toString());
			sb.append('|');
			sb.append(entry.getEvent().size());
			sb.append(" items]\n");
		}
		return sb.toString();
	}

}
