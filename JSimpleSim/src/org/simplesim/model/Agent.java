/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import org.simplesim.core.scheduling.Time;

/**
 * 
 *
 */
public interface Agent {
	
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
	 * Note: Do not invoke from outside the simulation loop!
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

}
