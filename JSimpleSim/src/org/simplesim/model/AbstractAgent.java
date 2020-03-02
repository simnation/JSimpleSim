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
package org.simplesim.model;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.simplesim.core.dynamic.ChangeRequest;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.DynamicDecorator;

/**
 * Implements all basic functionality of an agent.
 * <p>
 * Agents are the acting entities of the simulation model. They should implement
 * a strategy within their {@link #doEvent(Time)} method. This is usually done
 * by the following five steps:
 * <ol>
 * <li>read the messages from the inports
 * ({@link org.simplesim.core.routing.Message})
 * <li>modify the agent's state ({@link AgentState})
 * <li>compute output and write messages to other entities to the outports
 * <li>add events to the internal event queue if necessary
 * ({@link org.simplesim.core.scheduling.IEventQueue})
 * <li>return the time of the next local event
 * ({@link org.simplesim.core.scheduling.Time})
 * </ol>
 * <p>
 * Agents are always embedded in an {@link AbstractDomain} for
 * compartmentalization. If implemented, the agent may also refer to an
 * information board of its parent domain or the root domain for additional
 * external information.
 *
 * @param <S> type of the agent state containing all state variables
 * @param <E> type of the events
 */
public abstract class AbstractAgent<S extends AgentState, E> extends BasicModelEntity {

	/** the internal state of the agent */
	private final S state;

	/** the local event queue of the agent */
	private final IEventQueue<E> leq;
	
	/** Queue for model change requests, only used by dynamic simulators. */
	private final static ConcurrentLinkedDeque<ChangeRequest> queue=new ConcurrentLinkedDeque<>();
	
	/** Flag to indicate if the simulation is running. */
	private static boolean simulationIsRunning=false;
	
	/**
	 * Exception to be thrown if an unknown event occurs is returned from the event queue
	 */
	@SuppressWarnings("serial")
	public static class UnknownEventType extends RuntimeException {
		public UnknownEventType(String message) {
			super(message);
		}
	}
	
	/**
	 * Exception to be thrown if an incoming message cannot be handled 
	 */
	@SuppressWarnings("serial")
	public static class UnhandledMessageType extends RuntimeException {
		public UnhandledMessageType(String message) {
			super(message);
		}
	}

	/**
	 * Sets the agent's local event queue and the internal state.
	 * <p>
	 * Note: Generally, a {@link HeapEventQueue} will work best as local event
	 * queue.
	 *
	 * @param queue the local event queue
	 * @param s     the state of the agent
	 */
	public AbstractAgent(IEventQueue<E> queue, S s) {
		state=s;
		leq=queue;
	}

	public AbstractAgent(S s) {
		this(new HeapEventQueue<E>(),s);
	}

	/**
	 * Gets the agent's state containing all internal variables
	 *
	 * @return the agent's internal state
	 */
	public final S getState() {
		return state;
	}

	/**
	 * Gets the local event queue.
	 * <p>
	 * Note: The event queue should only be manipulated by this agent itself.
	 *
	 * @return the local event queue
	 */
	protected final IEventQueue<E> getEventQueue() {
		return leq;
	}

	/**
	 * Returns the time of the next internal event.
	 * <p>
	 * This method is called by the simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	public final Time getTimeOfNextEvent() {
		return leq.getMin();
	}

	/**
	 * Calculates new outputs from the available inputs and implements the agent's
	 * strategy.
	 * <p>
	 * This method is called by the simulator every time this agent is scheduled as
	 * an event in the global event queue. Outputs are basically massages that have
	 * to be put onto the outport of this agent. It should implement the general
	 * strategy of the agent and will acquire the actual simulation time from the
	 * simulator.
	 * <p>
	 * This method is supposed to do the following steps:
	 * <ul>
	 * <li>read the messages from the inports
	 * <li>modify the agent's state
	 * <li>compute output and write messages to other entities to the outports
	 * <li>add events to the internal event queue if necessary
	 * <li>return the time of the next local event (=next time to call this method)
	 * </ul>
	 * If implemented, the agent may also refer <i>read-only</i> to a bulletin board
	 * implementation of its parent domain or the root domain for additional
	 * external parameters
	 * <p>
	 *
	 * @param time current simulation time
	 *
	 * @return time of the next event (tone)
	 *
	 * @see AgentState
	 * @see IBulletinBoard
	 * @see org.simplesim.core.scheduling.Time Time
	 * @see org.simplesim.core.scheduling.IEventQueue IEventQueue
	 * @see org.simplesim.core.routing.Message Message
	 * @see org.simplesim.core.routing.AbstractPort AbstractPort
	 */
	protected abstract Time doEvent(Time time);

	/**
	 * Calls the {@code doEvent} method.
	 * <p>
	 * Is called by the simulator at {@link getTimeOfNextEvent}
	 *
	 * @param time the current simulation time
	 *
	 * @return time of the next event (tone)
	 */
	public final Time doEventSim(Time time) {
		return doEvent(time);
	}
	
	/**
	 * Provide logging functionality
	 * 
	 * @param time current time stamp
	 * @param msg additional message to output
	 */
	public void log(Time time, String msg) {
		StringBuffer sb=new StringBuffer();
		sb.append('[');
		sb.append(this.getFullName());
		sb.append(']');
		sb.append(time.toString());
		sb.append(msg);
		System.out.println(sb.toString());
	}

	/**
	 * Sets the status of simulation run.<br>
	 * Static method and variable to be accessible for all entities of the simulation model
	 *
	 * @param toggle the status of the simulation, {@code true} means simulation is running
	 */
	public static final void toggleSimulationIsRunning(boolean toggle) {
		simulationIsRunning=toggle;
	}

	/**
	 * Gets the status of simulation run.<br>
	 * Static method and variable to be accessible for all entities of the simulation model
	 *
	 * @return current simulation status, {@code true} means simulation is running
	 */
	public static final boolean isSimulationRunning() {
		return simulationIsRunning;
	}
	
	/**
	 * Add a model change request to the queue
	 * <p>
	 * Change request are processed by a dynamic simulator after each simulation cycle. Has no effect when using other
	 * simulator implementations.
	 * <p>
	 * This method is thread-safe.
	 *
	 * @param cr the request
	 * @see DynamicDecorator
	 */
	public static void addModelChangeRequest(ChangeRequest cr) {
		queue.add(cr);
	}

	/**
	 * Removes first model change request in queue
	 * <p>
	 * Should only be used by a dynamic simulator implementation and by an agent
	 * <p>
	 * This method is thread-safe.
	 *
	 * @param sim the simulator instance
	 * @return next model change request or null if queue is empty
	 */
	public static ChangeRequest pollModelChangeRequest() {
		return queue.poll();
	}

}
