/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.simulator;

import java.util.List;

import org.simplesim.core.observation.Listener;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.simulator.AbstractSimulator.InvalidSimulatorStateException;

/**
 * Basic simulator functionality
 *
 */
public interface Simulator {

	/**
	 * Starts a simulation run
	 *
	 * @param stop simulation time when the simulation should stop
	 * @exception InvalidSimulatorStateException if there is an error during simulation
	 */
	void runSimulation(Time stop);

	AbstractDomain getRootDomain();

	Time getSimulationTime();

	List<AbstractAgent<?, ?>> getCurrentEventList();

	/**
	 * Registers an {@code EventsProcessedListener} to be called after each simulation cycle.
	 * 
	 * @param listener the {@code Listener} implementation to be added
	 */
	void registerEventsProcessedListener(Listener<AbstractSimulator> listener);

	/**
	 * Unregisters an {@code EventsProcessedListener}.
	 * 
	 * @param listener the {@code Listener} implementation to be removed
	 */
	void unregisterEventsProcessedListener(Listener<AbstractSimulator> listener);

}
