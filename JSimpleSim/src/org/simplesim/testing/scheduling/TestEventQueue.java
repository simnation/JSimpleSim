/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.testing.scheduling;

import java.util.List;

import org.simplesim.core.scheduling.AbstractEventQueue;
import org.simplesim.core.scheduling.EventQueueEntry;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.PriorityEventQueue;
import org.simplesim.core.scheduling.SortedListEventQueue;
import org.simplesim.core.scheduling.SortedListEventQueue;
import org.simplesim.core.scheduling.Time;

/**
 * @author Rene Kuhlemann
 *
 */
public class TestEventQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final IEventQueue<String> eq=new PriorityEventQueue<>();
		populateEventQueue(eq,100);
		System.out.println(eq.toString());
		System.out.println((eq.dequeue("Event0047").getTime().toString()));
		System.out.println(eq.toString());
	}

	private static void populateEventQueue(IEventQueue<String> eq, int countEntries) {
		for (int index=0; index<countEntries; index++) {
			String event=String.format("Event%04x",index);
			eq.enqueue(event,new Time((int) (Math.random()*Time.DAY)));
		}
	}
	
	public static String toString(List<EventQueueEntry<String>> col) {
		StringBuffer sb=new StringBuffer();
		for (EventQueueEntry<?> entry : col) {
			sb.append('[');
			sb.append(entry.getTime().toString());
			sb.append('|');
			sb.append(entry.getEvent().toString());
			sb.append("]\n");
		}
		return sb.toString();
	}

}
