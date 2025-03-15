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

import java.util.PriorityQueue;

/**
 * Priority queue implementation of the {@code EventQueue} interface.
 * <p>
 * Adapter class wrapping a {@code PriorityQueue} to hold the data, internally
 * stored in a binary heap structure. Most operations are done in O(log n), but
 * searches like {@code dequeue(E)} and {@code getTime(E)} take O(n). Look up of
 * the minimal time {@code getMin()} is done in O(1) but dequeuing has a
 * complexity of O(log n).
 * <p>
 * Note: This queue type seems to perform best for dequeuing (and requeuing)
 * <i>single</i> events with minimal time stamp ({@link #dequeue()}). It might
 * be suitable as local and global event queue.
 *
 * @param <E> event type
 *
 * @see PriorityQueue
 */
public class HeapEventQueue<E> extends AbstractEventQueue<E, PriorityQueue<EventQueueEntry<E>>> {

	/**
	 * Default constructor initializing the queue
	 */
	public HeapEventQueue() {
		super(new PriorityQueue<>());
	}

	@Override
	public Time getMin() {
		return getQueue().peek().getTime();
	}

	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		return getQueue().poll().getEvent();
	}

}
