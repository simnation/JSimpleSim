/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.model;

import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.TimeStepSimulator;

/**
 * Implements all basic functionality of an agent.
 * <p>
 * Agents are the acting entities of the simulation model. They should implement
 * a strategy within their {@link #doEvent(Time)} method. This is ususally done
 * by the following five steps:
 * <ol>
 * <li>read the messages from the inports ({@link org.simplesim.core.routing.Message})
 * <li>modify the agent's state ({@link IAgentState})
 * <li>compute output and write messages to other entities to the outports 
 * <li>add events to the internal event queue if necessary ({@link org.simplesim.core.scheduling.IEventQueue})
 * <li>return the time of the next local event ({@link org.simplesim.core.scheduling.Time})
 * </ol><p>
 * Agents are always embedded in an {@link AbstractDomain} for compartmentalization.
 * If implemented, the agent may also refer to the bulletin boards of its parent
 * domain or the root domain ({@link IBulletinBoard}) for additional external information.
 *
 * @author Rene Kuhlemann
 * @param <S> type of the state
 * @param <E> type of the events
 */
public abstract class AbstractAgent<S extends IAgentState, E> extends BasicModelEntity {

	/** the internal state of the agent */
	private final S state;

	/** the local event queue of the agent */
	private final IEventQueue<E> leq;

	/**
	 * Sets agent identity and creates the internal state and event queue by using
	 * the protected create methods.
	 *
	 * @param name    the name of the entity, may be null or empty
	 * @param address the address of the entity within the model hierarchy, may be
	 *                null
	 */
	public AbstractAgent(String name, int[] addr) {
		super(name,addr);
		state=createState();
		leq=createInternalEventQueue();
	}

	/**
	 * Initializes agent using identification by int array (like IP-address)
	 *
	 * @param address the address of the entity within the model hierarchy
	 */
	public AbstractAgent(int[] addr) {
		this(null,addr);
	}

	/**
	 * Initializes agent using identification by a naming string
	 *
	 * @param name the name of the entity, may be empty
	 */
	public AbstractAgent(String name) {
		this(name,null);
	}

	/**
	 * Creates the agent state containing all internal variables.
	 * <p>
	 * This method must be overridden in descendant classes to create the individual
	 * state specific for this agent. This method should not be called from any
	 * other method than the constructor!
	 *
	 * @return a new agent state
	 */
	protected abstract S createState();

	/**
	 * Gets the agent's state containing all internal variables
	 *
	 * @return the agent's internal state
	 */
	public final S getState() {
		return state;
	}

	/**
	 * Creates a new local event queue for this agent.
	 * <p>
	 * This method must be overridden in descendant classes to create the specific
	 * type of event queue for this agent. This method should not be called from any
	 * other method than the constructor!
	 * <p>
	 * Note: Generally, a {@link SortedEventQueue} will work best as local event
	 * queue.
	 *
	 * @return a new event queue, may be null when using the
	 *         {@link TimeStepSimulator}
	 */
	protected abstract IEventQueue<E> createInternalEventQueue();

	/**
	 * Gets the local event queue.<p>
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
	 * If implemented, the agent may also refer to the bulletin boards of its parent
	 * domain or the root domain
	 * <p>
	 *
	 * @param time current simulation time
	 *
	 * @return time of the next event (tone)
	 *
	 * @see IAgentState
	 * @see IBulletinBoard
	 * @see org.simplesim.core.scheduling.Time Time
	 * @see org.simplesim.core.scheduling.IEventQueue IEventQueue
	 * @see org.simplesim.core.routing.Message Message
	 * @see org.simplesim.core.routing.AbstractPort AbstractPort
	 */
	protected abstract Time doEvent(final Time time);

	/**
	 * Calls the {@code doEvent} method.
	 * <p>
	 * Is called by the simulator at {@link getTimeOfNextEvent}
	 *
	 * @param time the current simulation time
	 *
	 * @return time of the next event (tone)
	 */
	public final Time doEventSim(final Time time) {
		return doEvent(time);
	}

}
