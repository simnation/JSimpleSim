/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.scheduling;

import org.simplesim.core.scheduling.Time;

/**
 * Entry of any event queue derived from {@link IEventQueue}. Setters can be used by
 * event queue implementations to reuse entries.
 * Outside the package {@linkplain org.simplesim.core.scheduling} this class can be considered
 * as <i>immutable</i> and thus thread-safe.
 * 
 * @author Rene Kuhlemann
 *
 */
public final class EventQueueEntry<E> implements Comparable<EventQueueEntry<E>> {
	
		private E event; // the event as such
		private Time time; // time stamp of the event.

		public EventQueueEntry(E e,Time t) {
			event=e;
			time=t;
		}

		public E getEvent() {
			return event;
		}

		public Time getTime() {
			return time;
		}

		void setTime(Time t) {
			time=t;
		}
		
		void setEvent(E e) {
			event=e;
		}
		
		public String toString() {
			return "["+getTime().toString()+"|"+getEvent().toString()+"]";
		}
		
		public int compareTo(EventQueueEntry<E> other) {
			return this.getTime().compareTo(other.getTime());
		}


}
