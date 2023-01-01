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
package org.simplesim.simulator;

import org.simplesim.core.messaging.ForwardingStrategy;
import org.simplesim.core.messaging.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.Agent;

/**
 * Simulator for sequential time step simulation
 * <p>
 * This simulator calls all agents of a model at equidistant time steps. The
 * {@code doEventSim} method of the agents is called sequentially every
 * {@code timeStep}.
 * <p>
 * This implementation is especially useful to run cellular automata.
 *
 */
public class SequentialTSSimulator extends AbstractSimulator {

	// the constant time step, no event queue
	private final Time timeStep=Time.TICK;

	public SequentialTSSimulator(AbstractDomain rt, ForwardingStrategy forwarding) {
		super(rt,null,forwarding);
	}

	/**
	 * Quick start constructor of a sequential time-step simulator with a given model
	 * <p>
	 * Uses {@code RecursiveMessageForwarding} as default option.
	 *
	 * @param root the root domain of the model
	 */
	public SequentialTSSimulator(AbstractDomain root) {
		this(root,new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		// list of all agents processed in the last event cycle
		setCurrentEventList(getRootDomain().listAllAgents(true));
		setSimulationTime(Time.ZERO);
		while (getSimulationTime().compareTo(stop)<0) {
			AbstractAgent.toggleSimulationIsRunning(true);
			// part I: process all current events by calling the agents' doEvent method
			// in time step, iterate over ALL agents, ignore time of next event
			for (Agent agent : getCurrentEventList()) agent.doEvent(getSimulationTime());
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			AbstractAgent.toggleSimulationIsRunning(false);
			hookEventsProcessed();
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(getTimeStep()));
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
	}

	public Time getTimeStep() { return timeStep; }

}
