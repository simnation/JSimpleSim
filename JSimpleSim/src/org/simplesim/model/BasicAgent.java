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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.simplesim.core.dynamic.ChangeRequest;
import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
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
 * ({@link org.simplesim.core.messaging.Message})
 * <li>modify the agent's state ({@link State})
 * <li>compute output and write messages to other entities to the outports
 * <li>add events to the internal event queue if necessary
 * ({@link org.simplesim.core.scheduling.EventQueue})
 * <li>return the time of the next local event
 * ({@link org.simplesim.core.scheduling.Time})
 * </ol>
 * <p>
 * Agents are always embedded in an {@code Domain} for compartmentalization.
 *
 * @param <S> type of the agent state containing all state variables
 * @param <E> type of the events
 *
 * @see BasicDomain
 * @see EventQueue
 */
public abstract class BasicAgent<S extends State, E> extends BasicModelEntity implements Agent {

	/** the internal state of the agent */
	private final S state;

	/** the local event queue of the agent */
	private final EventQueue<E> leq;

	// private Listener<Agent> bel=DUMMY_LISTENER; 
	/** After event listener for agent instrumentation */
	private Listener<Agent> ael=DUMMY_LISTENER;

	/** Queue for model change requests, only used by dynamic simulators. */
	private final static Queue<ChangeRequest> queue=new ConcurrentLinkedDeque<>();

	/** Flag to indicate if the simulation is running. */
	private static volatile boolean simulationIsRunning=false;

	/** Dummy listener for initialization of ael and bel */
	private static final Listener<Agent> DUMMY_LISTENER=(time, source) -> {};

	@SuppressWarnings("serial")
	public static final class UnknownEventType extends RuntimeException {
		public UnknownEventType(String msg) {
			super(msg);
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
	public BasicAgent(EventQueue<E> queue, S s) {
		state=s;
		leq=queue;
	}

	public BasicAgent(S s) {
		this(new HeapEventQueue<E>(),s);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final S getState() { return state; }

	/**
	 * Gets the local event queue.
	 * <p>
	 * Note: The event queue should only be manipulated by this agent itself.
	 *
	 * @return the local event queue
	 */
	protected EventQueue<E> getEventQueue() { return leq; }

	/**
	 * This method should only be called by the simulator.
	 *
	 */
	public final Time doEventSim(Time time) {
		doEvent(time);
		ael.notifyListener(time,this); // call after execution listener
		return getTimeOfNextEvent();
	}

	/**
	 * Returns the time of the next internal event.
	 * <p>
	 * This method is called by the simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	@Override
	public final Time getTimeOfNextEvent() { return leq.getMin(); }

	/**
	 * Sets a listener that is called after the agent's event processing
	 *
	 * @param listener the listener
	 */
	public final void setAfterExecutionListener(Listener<Agent> listener) { ael=listener; }

	/**
	 * Removes any listener activity.
	 *
	 */
	public final void resetAfterExecutionListener(Listener<Agent> listener) { ael=DUMMY_LISTENER; }
	
	/**
	 * Returns the current after event listener.
	 * <p>
	 * This can be used to add a new listener without losing the old one. Several listeners can be
	 * called by chaining them (listener 1 calls listener 2).
	 * 
	 * @param listener the listener
	 */
	public Listener<Agent> getAfterExecutionListener() { return ael; }

	/**
	 * Sets the status of simulation run.
	 * <p>
	 * Static method and variable to be accessible for all agents of the simulation
	 * model
	 *
	 * @param toggle the status of the simulation, {@code true} means simulation is
	 *               running
	 */
	public static final void toggleSimulationIsRunning(boolean toggle) {
		simulationIsRunning=toggle;
	}

	/**
	 * Gets the status of simulation run.
	 * <p>
	 * Static method and variable to be accessible for all entities of the
	 * simulation model
	 *
	 * @return current simulation status, {@code true} means simulation is running
	 */
	public static final boolean isSimulationRunning() { return simulationIsRunning; }

	/**
	 * Add a model change request to the queue
	 * <p>
	 * Change request are processed by a dynamic simulator after each simulation
	 * cycle. Has no effect when using other simulator implementations.
	 * <p>
	 * This method is thread-safe.
	 *
	 * @param cr the request
	 * @see DynamicDecorator
	 */
	public static final void addModelChangeRequest(ChangeRequest cr) {
		queue.add(cr);
	}

	/**
	 * Removes first model change request in queue
	 * <p>
	 * Should only be used by a dynamic simulator implementation and by an agent
	 * <p>
	 * This method is thread-safe.
	 *
	 * @return next model change request or null if queue is empty
	 */
	public static final ChangeRequest pollModelChangeRequest() {
		return queue.poll();
	}

}
