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

import java.util.List;

/**
 * Basic event queue interface for all queue implementations of the scheduling
 * package.
 * <p>
 * Event queues are priority queue data structures. In the realm of discrete
 * event simulation, the scheduling of events has been identified as a major
 * bottleneck (taking up to 40% computing time in large simulation models).
 * Thus, this interface is designed to cover all necessary functionality in a
 * minimalistic way. During simulation {@code #enqueue(E, Time)},
 * {@code #dequeueAll(Time)} and {@code #getMin()} are used most often,
 * presumably. A requeue operation can be done by dequeuing an event and then
 * enqueuing it again with a different time stamp.
 * <p>
 * Event queues might not be synchronized. Accessing unsynchronized event queues
 * from different threads may result in non-deterministic behavior. Please check
 * the documentation of the event queue you are going to use or synchronize
 * externally.
 * <p>
 * Please note that event queues have (injective) one-to-one mapping: There may be 
 * several events with equal time stamps, but no two events that are equal.
 * In other words: There must be only one time stamp per event!
 *
 * @param <E> type of events to be stored in the queue
 *
 */
public interface EventQueue<E> {

	/**
	 * Gets the minimal time stamp.
	 * <p>
	 * An empty event queue may result in non-deterministic behavior and is not checked!
	 *
	 * @return current minimal time stamp (does not test if queue is empty!)
	 * 
	 */
	Time getMin();

	/**
	 * Checks if the queue is empty.
	 *
	 * @return true if the queue is empty, false otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns the number of elements in the queue.
	 *
	 * @return number of queue entries
	 */
	int size();

	/**
	 * Gets the time of the given event but does not dequeue it.
	 *
	 * @param event the event to retrieve the time for
	 * @return time stamp of the event or null if the event does not exist
	 */
	Time getTime(E event);

	/**
	 * Enqueues an event at the given time.
	 * <p>
	 * Note that only distinct events may be added to the queue. If the very same
	 * event already exists, it may be overwritten.
	 *
	 * @param event the event to be added to the queue
	 * @param time  the time stamp of the event, must be a future time
	 */
	void enqueue(E event, Time time);

	/**
	 * Removes the entry of the given event.
	 *
	 * @param event the event to be removed from the queue
	 * @return time stamp of the dequeued event or null if the event was not part of
	 *         the queue
	 */
	Time dequeue(E event);

	/**
	 * Dequeues the event with the smallest time stamp.
	 * <p>
	 * If there are several events with the same time stamp, the result can be any
	 * of these events
	 * 
	 * @return event with the smallest time stamp or null if the queue is empty
	 * @see #getMin()
	 */
	E dequeue();

	/**
	 * Dequeues all elements with the smallest time stamp.
	 * A call to this method is equivalent to {@code dequeueAll(getMin())}.
	 * <p>
	 * An empty event queue may result in non-deterministic behavior and is not checked!
	 *
	 * @return a list containing all events with the minimum time stamp or an empty
	 *         list if the event queue is empty.
	 * 
	 * @see getMin()
	 * @see dequeueAll(Time)
	 */
	List<E> dequeueAll();

	/**
	 * Dequeues all elements with the given time stamp.
	 * <p>
	 * An empty event queue may result in non-deterministic behavior and is not checked!
	 * 
	 * @param time the time stamp of the events to dequeue
	 *
	 * @return a list containing all events with this time stamp or an empty list if
	 *         there are not events at the given time
	 */
	List<E> dequeueAll(Time time);

}
