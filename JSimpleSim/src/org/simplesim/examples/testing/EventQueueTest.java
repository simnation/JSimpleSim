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

import org.simplesim.core.scheduling.HashedBucketQueue;
import org.simplesim.core.scheduling.HeapBucketQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedBucketQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.MListEventQueue;
import org.simplesim.core.scheduling.Time;

/**
 * Performance test and comparison of various implementations of {@code IEventQueue}
 * <p>
 * All methods of the event queue are tested and evaluated. The size of the queue and number of test
 * are adjusted by changing the {@code countXXX} constants.
 *
 */
public class EventQueueTest {

	private static final int initTime=3*Time.DAY; 	// time interval for generating initial events
	private static final int aheadTime=Time.DAY;
	private static final int countTotal=150000; 	// number of total events in queue
	private static final int countSADE=1000;		// number of events for search and dequeue
	private static final int countRNE=10000;		// number of events for requeue current event
	
	private enum QUEUE_TYPE {
		HASHED_BUCKET_QUEUE("Hashed bucket queue", new HashedBucketQueue<String>()),
		HEAP_BUCKET_QUEUE("Heap bucket queue", new HeapBucketQueue<String>()),
		SORTED_BUCKET_QUEUE("Sorted bucket queue", new SortedBucketQueue<String>()),
		HEAP_EVENT_QUEUE("Heap event queue", new HeapEventQueue<String>()),
		MLIST_EVENT_QUEUE("MList event queue", new MListEventQueue<String>()),
		SORTED_EVENT_QUEUE("Sorted event queue", new SortedEventQueue<String>());
				

		private final String name;
		private final IEventQueue<String> eq;

		private QUEUE_TYPE(String name, IEventQueue<String> eq) {
			this.name=name;
			this.eq=eq;
		}

		public String getName() {
			return name;
		}

		public IEventQueue<String> getQueue() {
			return eq;
		}

	}

	private final QUEUE_TYPE qt;

	public EventQueueTest(QUEUE_TYPE queueType) {
		qt=queueType;
	}

	/**
	 * Initializes the event queue with {@code countTotal} events
	 * 
	 */
	public long populateEventQueue() {
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		final long startTime=System.nanoTime();
		for (int index=0; index<countTotal; index++) {
			final String event=String.format("Event%04x",index);
			qt.getQueue().enqueue(event,new Time((int) (Math.random()*initTime)));
		}
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * Test for requeuing the current event
	 * <p>
	 * Dequeues the current event and enqueues it again with a new time stamp, using 
	 * {@code dequeue()} and {@code enqueue(Time,E)} functionality.
	 * Does not change the size of the queue.
	 */
	public long doRequeueNextEvent() {
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		qt.getQueue().getMin(); // try getMin(), also fills the tiers in MList 
		final long startTime=System.nanoTime();
		for (int index=0; index<countRNE; index++) {
			final String event=qt.getQueue().dequeue();
			qt.getQueue().enqueue(event,new Time(initTime+((int) (Math.random()*aheadTime))));
		}
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * Test for look-up operations of an event queue
	 * <p>
	 * Searches for an event and requeues it with a new time stamp, using {@code getTime(E)}, 
	 * {@code dequeue(E)} and {@code enqueue(Time,E)} functionality. 
	 * Decreases the size of the queue by {@code countSADE} events.
	 */
	public long doSearchAndDequeueEvent() {
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		final long startTime=System.nanoTime();
		for (int index=1; index<countSADE; index++) {
			final String event=String.format("Event%04x",index);
			if (!qt.getQueue().getTime(event).equals(
					qt.getQueue().dequeue(event)))
				System.out.println("Inconsistent event queue!");
		}
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * Test for dequeuing all current events
	 * <p>
	 * Dequeues the all current events with the same time stamp, using 
	 * {@code dequeueAll()} functionality.
	 * The event queue will be empty after this method.
	 * 
	 */
	public long doDequeueAllNextEvents() {
		int count=0;
		int sum=0;
		System.gc(); // resetting GC to avoid test distortion by garbage collection
		final long startTime=System.nanoTime();
		while (!qt.getQueue().isEmpty()) {
			sum+=qt.getQueue().dequeueAll().size();
			count++;
		}
		System.out.print(", different time stamps: "+count+", events per time stamp: "+(sum/count)+", takes ");
		return ((System.nanoTime()-startTime)/1000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SortedMap<Long, String> performanceEnqueue=new TreeMap<>();
		final SortedMap<Long, String> performanceSADE=new TreeMap<>();
		final SortedMap<Long, String> performanceRNE=new TreeMap<>();
		final SortedMap<Long, String> performanceDequeueAll=new TreeMap<>();
		for (final QUEUE_TYPE eq : QUEUE_TYPE.values()) {
			System.out.println("Testing "+eq.getName());
			final EventQueueTest test=new EventQueueTest(eq);
			
			System.out.print("Bulding queue with a total of "+countTotal+" events with enqeue(Time,E): ");
			long runTime=test.populateEventQueue();
			performanceEnqueue.put(runTime,eq.getName());
			System.out.println(runTime+" ms");
			
			System.out.print("Requeueing "+countRNE+" events with dequeue() and enqeue(Time,E): ");
			runTime=test.doRequeueNextEvent();
			performanceRNE.put(runTime,eq.getName());
			System.out.println(runTime+" ms");
			
			System.out.print("Searching and dequeueing "+countSADE+" events with getTime(E) and dequeue(E): ");
			runTime=test.doSearchAndDequeueEvent();
			performanceSADE.put(runTime,eq.getName());
			System.out.println(runTime+" ms");
			
			System.out.print("Emptying queue with dequeueAll()");
			runTime=test.doDequeueAllNextEvents();
			performanceDequeueAll.put(runTime,eq.getName());
			System.out.println(runTime+" ms\n");
		}
		
		System.out.println("\nPerformance in descending order for enqeue(Time,E):");
		for (int i=0; i<QUEUE_TYPE.values().length; i++) {
			long time=performanceEnqueue.firstKey();
			System.out.println(performanceEnqueue.get(time)+" with "+time+" ms");
			performanceEnqueue.remove(time);
		}
		
		System.out.println("\nPerformance in descending order for dequeue() and enqeue(Time,E):");
		for (int i=0; i<QUEUE_TYPE.values().length; i++) {
			long time=performanceRNE.firstKey();
			System.out.println(performanceRNE.get(time)+" with "+time+" ms");
			performanceRNE.remove(time);
		}
		
		System.out.println("\nPerformance in descending order for getTime(E) and dequeue(E):");
		for (int i=0; i<QUEUE_TYPE.values().length; i++) {
			long time=performanceSADE.firstKey();
			System.out.println(performanceSADE.get(time)+" with "+time+" ms");
			performanceSADE.remove(time);
		}
		
		System.out.println("\nPerformance in descending order for dequeueAll():");
		for (int i=0; i<QUEUE_TYPE.values().length; i++) {
			long time=performanceDequeueAll.firstKey();
			System.out.println(performanceDequeueAll.get(time)+" with "+time+" ms");
			performanceDequeueAll.remove(time);
		}

		System.out.println("done.");
	}

}
