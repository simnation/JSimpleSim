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
package org.simplesim.simulator;

import org.simplesim.core.scheduling.Time;

/**
 * Represents the core functionality of a simulator
 *
 */
public interface ISimulator {

	
	/**
	 * Starts a simulation run.
	 * 
	 * @param stop simulation time when the simulation should stop
	 * 
	 * @exception NullPointerException if the event queue is empty before the stop time is reached
	 */
	void runSimulation(Time stop);

	Time getSimulationTime();

}
