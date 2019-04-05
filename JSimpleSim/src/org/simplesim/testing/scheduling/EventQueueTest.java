/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.testing.scheduling;

import java.util.List;

import org.simplesim.core.scheduling.HashedBucketQueue;
import org.simplesim.core.scheduling.HeapBucketQueue;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedBucketQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;

/**
 * @author Rene Kuhlemann
 *
 */
public class EventQueueTest {
	
	private final IEventQueue<String> eq;
	private Time lastMin;
	
	private static final int countTotal=100000;
	private static final int countSADE=100;
	private static final int countDNE=100;
	
	public EventQueueTest(IEventQueue<String> eventqueue) {
		eq=eventqueue;
	}

	public void populateEventQueue(int countEntries, long maxTime) {
		System.out.println("Generating event queue with "+countEntries+" entries..."); 
		for (int index=0; index<countEntries; index++) {
			final String event=String.format("Event%04x",index);
			eq.enqueue(event,new Time((int) (Math.random()*maxTime)));
		}
		lastMin=eq.getMin();
	}
	
	public void doSearchAndDequeueEvent() {
		System.out.println("Actual minTime: "+eq.getMin().toString());
		System.out.print("Searching and dequeueing "+countSADE+" events..."); 
		for (int index=0; index<countSADE; index++) {
			final String event=String.format("Event%04x",index);
			final Time time=eq.getTime(event);
			assert time.equals(eq.dequeue(event));
		}
		System.out.println("done.");
	}	
	
	public void doDequeueNextEvent() {
		System.out.println("Actual minTime: "+eq.getMin().toString());
		System.out.print("Dequeueing "+countDNE+" events with minimal timestamp..."); 
		for (int index=0; index<countDNE; index++) {
			eq.dequeue();
		}
		System.out.println("done.");
	}
	
	public void doDequeueAllNextEvents() {
		System.out.println("Actual minTime: "+eq.getMin().toString());
		System.out.print("Dequeueing remaining events using dequeueAll"); 
		int count=0;
		int sum=0;
		while (!eq.isEmpty()) {
			if (lastMin.compareTo(eq.getMin())>0) {
				System.out.println("Time stamp error. Previous time: "+
						lastMin.toString()+", acutal min: "+eq.getMin().toString());
				break;
			}		
			lastMin=eq.getMin();
			sum+=eq.dequeueAll().size();
			count++;
		}
		System.out.println("Average bucket size was: "+sum/count);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.gc();
		final long startTime=System.nanoTime();
		final EventQueueTest test=new EventQueueTest(new SortedEventQueue<String>());
		test.populateEventQueue(countTotal, Time.DAY);
		test.doSearchAndDequeueEvent();
		test.doDequeueNextEvent();
		test.doDequeueAllNextEvents();
		System.out.println("Runtime was: "+(System.nanoTime()-startTime)+" ns");
	}

}
