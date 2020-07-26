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

import java.util.ArrayList;
import java.util.List;

/**
 * Sorted list implementation of the {@code EventQueue} interface.
 * <p>
 * A <i>descending</i> sorted list is used to hold the data, internally stored
 * in an {@code ArrayList}. Look-up operations are done by binary search,
 * leading to a complexity of O(log n). {@code enqueue()} and {@code dequeue(E)}
 * operations also need to move all elements with a lesser time stamp (max.
 * O(n)). {@code dequeue()}, {@code getMin()} and {@code dequeueAll()} are done
 * in O(1), since the elements with the least time stamp are always placed at
 * the end of the list (descending sorting order).
 * <p>
 * Note: The performance of this queue type declines with the number of entries. Thus, this 
 * implementation seems better suited for local event queues, which often are memory-critical
 * and generally hold fewer events.
 *
 * @param <E> event type
 * 
 * @see HeapEventQueue
 */
public class SortedEventQueue<E> extends AbstractEventQueue<E,ArrayList<EventQueueEntry<E>>> {

	private static final int DEFAULT_LIST_SIZE=8;

	public SortedEventQueue() {
		super(new ArrayList<>(DEFAULT_LIST_SIZE));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#getMin()
	 */
	@Override
	public Time getMin() {
		return getQueue().get(size()-1).getTime();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeue()
	 */
	@Override
	public E dequeue() {
		if (isEmpty()) return null;
		return getQueue().remove(size()-1).getEvent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.simplesim.core.scheduling.AbstractEventQueue#enqueue(java.lang.Object,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueue(E event, Time time) {
		getQueue().add(getPosition(time),new EventQueueEntry<E>(time,event));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public List<E> dequeueAll(Time time) {
		if (time.equals(getMin())) return dequeueAll();
		return dequeueAll(getPosition(time)-1,time);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.core.scheduling.EventQueue#dequeueAll()
	 */
	@Override
	public List<E> dequeueAll() {
		return dequeueAll(size()-1,getMin());
	}

	/**
	 * Dequeues all entries from position {@code pos} with the given time stamp
	 *
	 * @param pos  - right-most position to start dequeuing of elements
	 * @param time - time stamp of elements to be dequeued
	 *
	 * @return a list with all entries at the given position with the given time
	 *         stamp
	 */
	private List<E> dequeueAll(int pos, Time time) {
		final List<E> result=new ArrayList<>();
		for (int index=pos; index>=0; index--) {
			if (!getQueue().get(index).getTime().equals(time)) break;
			result.add(getQueue().remove(index).getEvent());
		}
		return result;
	}

	/**
	 * Binary search to find the right-most position in a descending sorted list.
	 *
	 * @param time time stamp of the element to insert
	 * @return insertion position of entry, right-most position in case of similar
	 *         values
	 */
	private int getPosition(Time time) {
		int right=size(), left=0;
		while (left<right) {
			final int mid=(left+right)>>1; // = left + (right - left) / 2;
			if (getQueue().get(mid).getTime().compareTo(time)<0) right=mid;
			else left=mid+1;
		}
		return left;
	}

}
