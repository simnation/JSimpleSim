/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.examples.testing;

import java.util.SortedMap;
import java.util.TreeMap;

import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.HashedBucketQueue;
import org.simplesim.core.scheduling.HeapBucketQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.MultiLevelBucketQueue;
import org.simplesim.core.scheduling.MultiLevelEventQueue;
import org.simplesim.core.scheduling.SortedBucketQueue;
import org.simplesim.core.scheduling.Time;

/**
 * Performance test and comparison of various implementations of
 * {@code EventQueue}
 * <p>
 * Enqueue and dequeue functionality is used most often in simulation
 * applications. So, this test focuses on a large number of events with repeated
 * queue usage.
 *
 */
public class LargeQueueTest {

	private static final int countTotal=160000; 	// number of total events in queue
	private static final int countReuse=10;			// number of consecutive usage
	private static final int deltaTime=countTotal>>4; 	// time interval for generating initial events

	private enum QueueType {
		HASHED_BUCKET_QUEUE("Hashed bucket queue", new HashedBucketQueue<String>()),
		HEAP_BUCKET_QUEUE("Heap bucket queue", new HeapBucketQueue<String>()),
		SORTED_BUCKET_QUEUE("Sorted bucket queue", new SortedBucketQueue<String>()),
		HEAP_EVENT_QUEUE("Heap event queue", new HeapEventQueue<String>()),
		MLIST_EVENT_QUEUE("MList bucket queue", new MultiLevelBucketQueue<String>()),
		MLIST_EVENT_QUEUE2("MList event queue", new MultiLevelEventQueue<String>());
		
		
		private final String name;
		private final EventQueue<String> eq;

		QueueType(String name, EventQueue<String> eq) {
			this.name=name;
			this.eq=eq;
		}

		public String getName() { return name; }

		public EventQueue<String> getQueue() { return eq; }

	}

	private final QueueType qt;

	public LargeQueueTest(QueueType queueType) {
		qt=queueType;
	}

	/**
	 * Initializes the event queue with {@code countTotal} events
	 *
	 */
	public long populateEventQueue(long initTime) {
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		final long startTime=System.nanoTime();
		for (int index=0; index<countTotal; index++) {
			final String event=String.format("Event%04x",index);
			qt.getQueue().enqueue(event,new Time(initTime+(long) (Math.random()*deltaTime)));
		}
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * Test for dequeuing all current events
	 * <p>
	 * Dequeues the all current events with the same time stamp, using
	 * {@code dequeueAll()} functionality. The event queue will be empty after this
	 * method.
	 *
	 */
	public long doDequeueAllNextEvents() {
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		final long startTime=System.nanoTime();
		while (!qt.getQueue().isEmpty()) {
			qt.getQueue().dequeueAll().size();
		}
		//System.out.print(", different time stamps: "+count+", events per time stamp: "+(sum/count)+", takes ");
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SortedMap<Long, String> performanceEnqueue=new TreeMap<>();
		final SortedMap<Long, String> performanceDequeueAll=new TreeMap<>();
		final SortedMap<Long, String> performanceTotal=new TreeMap<>();
		for (final QueueType eq : QueueType.values()) {
			System.out.print("Testing "+eq.getName());
			final LargeQueueTest test=new LargeQueueTest(eq);
			long time1=0, time2=0;
			long startTime=0;

			for (int round=0; round<countReuse; round++) {
				time1+=test.populateEventQueue(startTime);
				time2+=test.doDequeueAllNextEvents();
				System.out.print(".");
				startTime+=deltaTime;
			}
			time1/=countReuse;
			time2/=countReuse;
			System.out.println("\nBulding queue with a total of "+countTotal+" events: "+time1+" ms");
			performanceEnqueue.put(time1,eq.getName());
			System.out.println("Emptying queue with dequeueAll(): "+time2+" ms");
			performanceDequeueAll.put(time2,eq.getName());
			performanceTotal.put(time1+time2,eq.getName());
		}

		System.out.println("\nPerformance in descending order for enqeue(Time,E):");
		for (int i=0; i<QueueType.values().length; i++) {
			long time=performanceEnqueue.firstKey();
			System.out.println(performanceEnqueue.get(time)+" with "+time+" ms");
			performanceEnqueue.remove(time);
		}

		System.out.println("\nPerformance in descending order for dequeueAll():");
		for (int i=0; i<QueueType.values().length; i++) {
			long time=performanceDequeueAll.firstKey();
			System.out.println(performanceDequeueAll.get(time)+" with "+time+" ms");
			performanceDequeueAll.remove(time);
		}
		
		System.out.println("\nPerformance in descending order for total execution time:");
		for (int i=0; i<QueueType.values().length; i++) {
			long time=performanceTotal.firstKey();
			System.out.println(performanceTotal.get(time)+" with "+time+" ms");
			performanceTotal.remove(time);
		}

		System.out.println("done.");
	}

}
