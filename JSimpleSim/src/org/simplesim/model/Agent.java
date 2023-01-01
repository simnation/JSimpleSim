/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import org.simplesim.core.messaging.AbstractPort;
import org.simplesim.core.scheduling.Time;

/**
 * 
 *
 */
public interface Agent extends ModelEntity {
	
	/**
	 * Returns the agent's state containing all internal variables
	 *
	 * @return the agent's internal state
	 */
	<S extends State> S getState();
	
	/**
	 * Returns the time of the next internal event.
	 * <p>
	 * This method is called by the simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	public Time getTimeOfNextEvent();
	
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
	 * external parameters.
	 * <p>
	 * <b>Do not invoke from outside the simulation loop!</b>
	 *
	 * @param time current simulation time
	 * @return time of the next event (tone)
	 *
	 * @see State
	 * @see Time
	 * @see org.simplesim.core.scheduling.EventQueue EventQueue
	 * @see org.simplesim.core.messaging.Message Message
	 * @see org.simplesim.core.messaging.AbstractPort AbstractPort
	 */
	Time doEvent(Time time);
	
	/**
	 * Returns the agent's inport
	 * <p>
	 * The inport contains all incoming messages. 
	 * This method is called by the message forwarding algorithm and when connecting agents.
	 *
	 * @return the inport
	 */
	AbstractPort getInport();

	/**
	 * Returns the agent's outport
	 * <p>
	 * The outport contains all outgoing messages.
	 * This method is called by the message forwarding algorithm and when connecting agents.
	 *
	 * @return the outport
	 */
	AbstractPort getOutport();
	
	/**
	 * Returns the level of the current domain within the model hierarchy
	 * <p>
	 * Models may be organized in a hierarchy, so that each entity resides in a definite domain level of the model tree.
	 * The level information is generated when the getLevel() method is called first. The level of the root node is
	 * always {@value BasicModelEntity#ROOT_LEVEL}.
	 * 
	 * @return the level of this entity in the model hierarchy
	 */
	int getLevel();
	
	/**
	 * Gets the entity address. Can be null.
	 * <p>
	 * Note: The address of the root domain is {@code int[0]}. Another dimension has to be added per model level. The
	 * value of each dimension is the index within the corresponding level.
	 *
	 * @return the address
	 */
	int[] getAddress(); 
	
	/**
	 * Returns the name of this model entity
	 * <p>
	 * Returns an empty string as default, may be overridden in derived classes.
	 *
	 * @return the name of this model entity, may be an empty string but not null
	 */
	String getName();
	
	/**
	 * Returns the full name of a model, concatenating the names of the parent entities.
	 * <p>
	 * Example: If A and B are parents of this entity and this entity is named C, then the full name is A.B.C
	 *
	 * @return the full name of this entity
	 */
	String getFullName();
	
	/**
	 * Returns the parent of this model.
	 *
	 * @return the parent of this model
	 */
	AbstractDomain getParent();
	

}
