/**
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

/**
 * Provides a coupling between a time stamp and a corresponding event.
 * <p>
 * This class is immutable and thus thread-safe.
 * 
 * @see Time
 *
 */
final class EventQueueEntry<E> implements Comparable<EventQueueEntry<E>> {

	private final Time time; // time stamp of the event.
	private final E event; // the event as such
	
	public EventQueueEntry(Time t, E e) {
		event=e;
		time=t;
	}

	public E getEvent() {
		return event;
	}

	public Time getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "["+getTime().toString()+"|"+getEvent().toString()+"]";
	}

	@Override
	public int compareTo(EventQueueEntry<E> other) {
		return this.getTime().compareTo(other.getTime());
	}

}
