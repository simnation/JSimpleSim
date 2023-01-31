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

import org.simplesim.core.dynamic.AddEntityRequest;
import org.simplesim.core.dynamic.ChangeDomainRequest;
import org.simplesim.core.dynamic.ChangeRequest;
import org.simplesim.core.dynamic.ConnectPortRequest;
import org.simplesim.core.dynamic.DisconnectPortRequest;
import org.simplesim.core.dynamic.ReconnectPortRequest;
import org.simplesim.core.dynamic.RemoveEntityRequest;
import org.simplesim.core.messaging.Port;
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

	interface DomainChangeStrategy {
		void sendDomainChangeRequest(BasicDomain dest);

		void sendEntityRemoveRequest();
	}

	/** the internal state of the agent */
	private final S state;

	/** the local event queue of the agent */
	private final EventQueue<E> leq;

	private DomainChangeStrategy dcs=new DomainChangeStrategy() {

		@Override
		public void sendDomainChangeRequest(BasicDomain dest) {
			pushModelChangeRequest(new ChangeDomainRequest(BasicAgent.this,dest));
		}

		@Override
		public void sendEntityRemoveRequest() {
			pushModelChangeRequest(new RemoveEntityRequest(BasicAgent.this));
		}

	};

	/** Queue for model change requests, only used by dynamic simulators. */
	private final static Queue<ChangeRequest> queue=new ConcurrentLinkedDeque<>();

	/** Flag to indicate if the simulation is running. */
	private static volatile boolean simulationIsRunning=false;

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
	public S getState() { return state; }

	/**
	 * Returns the time of the next internal event.
	 * <p>
	 * This method is called by the simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	@Override
	public Time getTimeOfNextEvent() { return leq.getMin(); }

	/**
	 * Gets the local event queue.
	 * <p>
	 * Note: The event queue should only be manipulated by this agent itself.
	 *
	 * @return the local event queue
	 */
	protected EventQueue<E> getEventQueue() { return leq; }

	/**
	 * Moves this agent to an other domain.
	 * <p>
	 * A move request can only be issued by the agent itself. Agents cannot remove
	 * each other.
	 *
	 */
	protected void pushChangeDomainRequest(BasicDomain dest) {
		dcs.sendDomainChangeRequest(dest);
	}

	/**
	 * Remove this agent from the model.
	 * <p>
	 * A remove request can only be issued by the agent itself. Agents cannot remove
	 * eachothers.
	 *
	 */
	protected void pushRemoveEntityRequest() {
		dcs.sendEntityRemoveRequest();
	}

	/**
	 * Adds a NEW entity to the model during a simulation run.
	 * <p>
	 * Using this method increases the number of agents within the model.
	 *
	 */
	protected void pushAddEntityRequest(BasicModelEntity what, BasicDomain dest) {
		pushModelChangeRequest(new AddEntityRequest(what,dest));
	}

	/**
	 * Establishes a new port connection.
	 *
	 */
	protected void pushConnectPortRequest(Port from, Port to) {
		pushModelChangeRequest(new ConnectPortRequest(from,to));
	}

	/**
	 * Disconnects two already connected ports.
	 *
	 */
	protected void pushDisconnectPortRequest(Port from, Port to) {
		pushModelChangeRequest(new DisconnectPortRequest(from,to));
	}

	/**
	 * Switches a port connection.
	 *
	 */
	protected void pushReconnectPortRequest(Port port, Port oldTo, Port newTo) {
		pushModelChangeRequest(new ReconnectPortRequest(port,oldTo,newTo));
	}

	/**
	 * Only to be used by the {@code InstrumentationDecorator}.
	 *
	 */
	void setDomainChangeStrategy(DomainChangeStrategy strategy) { dcs=strategy; }

	/**
	 * Adds a model change request to the queue
	 * <p>
	 * Change request are processed by a dynamic simulator after each simulation
	 * cycle. Has no effect when using other simulator implementations.
	 * <p>
	 * This method is thread-safe.
	 *
	 * @param cr the request
	 *
	 */
	protected static final void pushModelChangeRequest(ChangeRequest cr) {
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
	 * @see DynamicDecorator
	 */
	public static final ChangeRequest pollModelChangeRequest() {
		return queue.poll();
	}

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

}
