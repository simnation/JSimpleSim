/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.model;

import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;

/**
 * To document
 * 
 * 
 * @author Rene Kuhlemann
 * @param <S> type of the state
 * @param <E> type of the events
 */
public abstract class AbstractAgent<S extends AbstractState, E> extends BasicModelEntity {

	// the internal state of the agent
	private final S state;

	// the internal local event queue of the agent
	private final IEventQueue<E> leq;

	/**
	 * Default constructor. This constructor creates the internal state and event
	 * queue by using the (protected) create methods. Both methods can be easily
	 * overridden to create any {@link AbstractState} state or {@link IEventQueue}
	 * event queue.
	 */
	public AbstractAgent(String name,int[] addr) {
		super(name,addr);
		state=createState();
		leq=createInternalEventQueue();
	}
	
	public AbstractAgent(int[] addr) {
		this(null,addr);
	}
	
	public AbstractAgent(String name) {
		this(name, null);
	}
	
	public AbstractAgent() {
		this(null,null);
	}	

	/**
	 * This method can be overridden in descendant classes for creating different
	 * types of state. (more specialized ones)
	 *
	 * @return a new state
	 */
	protected abstract S createState();

	public final S getState() {
		return state;
	}

	/**
	 * This method can be overridden in descendant classes for creating different
	 * types of event queues.
	 *
	 * @return a new event queue
	 */
	protected abstract IEventQueue<E> createInternalEventQueue();

	/**
	 * This method provides the internal event queue. It is protected to be used
	 * only by derived class implementations / ancestor classes.
	 * 
	 * @return internal event queue
	 */
	protected final IEventQueue<E> getEventQueue() {
		return (IEventQueue<E>) leq;
	}

	/**
	 * This method returns the time of the next internal event. It is called by the
	 * simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	public final Time getTimeOfNextEvent() {
		return leq.getMin();
	}

	/**
	 * The doEvent method is called by the simulation when an event is scheduled in
	 * the global event queue. It should implement the general strategy of the agent
	 * and will acquire the actual simulation time from the simulator. This method
	 * is supposed to do the following steps:
	 *
	 * - read the messages from the InPorts - modify the agent's state - add events
	 * to the internal event queue if necessary - compute the output and write the
	 * messages to the OutPorts
	 *
	 * @param current simulation time
	 *
	 * @return Time of the next event (tone)
	 */
	protected abstract Time doEvent(final Time time);

	/**
	 * The doEventSim method is called by the simulator at
	 * {@link getTimeOfNextEvent}
	 *
	 * @param time current simulation time
	 * 
	 * @return Time of the next event (tone)
	 */
	public final Time doEventSim(final Time time) {
		return doEvent(time);
	}

}
