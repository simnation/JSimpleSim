/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.simulator;

import java.util.List;

import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.Agent;
import org.simplesim.model.Domain;

/**
 * The simulator runs the simulation model. Its functionality comprises:
 * <ul>
 * <li>Maintaining the global event queue (geq)
 * <li>Scheduling of the agents' time of next events (tonie)
 * <li>Task management, starting the agents' {@code doEvent} methods
 * </ul>
 *
 */
public interface Simulator {

	/**
	 * Exception to be thrown if an invalid state occurs during simulation
	 */
	@SuppressWarnings("serial")
	static final class InvalidSimulatorStateException extends RuntimeException {
		public InvalidSimulatorStateException(String message) {
			super(message);
		}
	}

	/**
	 * Starts a simulation run
	 *
	 * @param stop simulation time when the simulation should stop
	 * @exception Simulator.InvalidSimulatorStateException if there is an error
	 *                                                     during simulation
	 */
	void runSimulation(Time stop);

	Domain getRootDomain();

	Time getSimulationTime();

	List<Agent> getCurrentEventList();

	/**
	 * Registers an {@code EventsProcessedListener} to be called after each
	 * simulation cycle.
	 *
	 * @param listener the {@code Listener} implementation to be added
	 */
	void registerEventsProcessedListener(Listener<Simulator> listener);

	/**
	 * Unregisters an {@code EventsProcessedListener}.
	 *
	 * @param listener the {@code Listener} implementation to be removed
	 */
	void unregisterEventsProcessedListener(Listener<Simulator> listener);

}
