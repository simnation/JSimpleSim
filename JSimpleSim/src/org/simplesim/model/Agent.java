/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import java.io.PrintStream;

import org.simplesim.core.scheduling.Time;

/**
 * Agents are the acting entities of the simulation model.
 * <p>
 * Agents should implement a strategy within their {@code doEvent} method and
 * provide the scheduler with the time of next internal event (tonie). Data
 * should be stored separately within the agent's state. Agents are always
 * embedded within a {@code Domain} for compartmentalization of the model.
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
	 * Returns the time of the next internal event (tonie).
	 * <p>
	 * This method is called by the simulator to update the global event queue.
	 *
	 * @return time of the next internal event
	 */
	Time getTimeOfNextEvent();

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
	 *
	 * @see State
	 * @see Time
	 * @see org.simplesim.core.scheduling.EventQueue EventQueue
	 * @see org.simplesim.core.messaging.Message Message
	 * @see org.simplesim.core.messaging.AbstractPort AbstractPort
	 */
	void doEvent(Time time);
	
	/**
	 * Provides simple logging functionality to a stream with time stamp, entity name and message output.
	 * <p>
	 * Can be used to redirect output to a log file. May be overloaded by a more sophisticated implementation.
	 * 
	 * @param stream stream to be used as output
	 * @param time current time stamp
	 * @param msg message to print 
	 */	
	default void log(PrintStream stream, Time time, String msg) {
		stream.println(time.toString()+this.toString()+": "+msg);
	}
	
	/**
	 * Provides simple logging functionality to a stream - only message output.
	 * <p>
	 * Can be used to redirect output to a log file. May be overloaded by a more sophisticated implementation.
	 * 
	 * @param stream stream to be used as output
	 * @param msg message to print 
	 */	
	default void log(PrintStream stream, String msg) {
		stream.println(msg);
	}

	/**
	 * Provides simple logging functionality to System.out with time stamp, entity name and message output.
	 * <p>
	 * Can be used to redirect output to a log file. May be overloaded by a more sophisticated implementation.
	 * 
	 * @param time current time stamp
	 * @param msg message to print 
	 */	
	default void log(Time time, String msg) {
		log(System.out,time,msg);
	}
	
	/**
	 * Provides simple logging functionality to System.out - only message output.
	 * <p>
	 * Can be used to redirect output to a log file. May be overloaded by a more sophisticated implementation.
	 * 
	 * @param msg message to print 
	 */	
	default void log(String msg) {
		log(System.out,msg);
	}
	
}
