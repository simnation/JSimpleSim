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

import java.util.List;

import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.messaging.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.BasicAgent;
import org.simplesim.model.BasicDomain;
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
public class SequentialTSSimulator extends BasicSimulator {

	// the constant time step, no event queue
	private final Time timeStep=Time.TICK;

	public SequentialTSSimulator(BasicDomain rt, MessageForwardingStrategy forwarding) {
		super(rt,null,forwarding);
	}

	/**
	 * Quick start constructor of a sequential time-step simulator with a given model
	 * <p>
	 * Uses {@code RecursiveMessageForwarding} as default option.
	 *
	 * @param root the root domain of the model
	 */
	public SequentialTSSimulator(BasicDomain root) {
		this(root,new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		setSimulationTime(Time.ZERO);
		List<Agent> cel=getRootDomain().listAllAgents(true); // cel=current event list
		boolean rebuildTaskList = false;

		while (getSimulationTime().compareTo(stop)<0) {
			BasicAgent.setSimulationIsRunning(true);
			// part 0: costly rebuild of list only if there are changes to the model
			if (rebuildTaskList) {
				cel = getRootDomain().listAllAgents(true);
				rebuildTaskList = false;
			}
			// part I: process all current events by calling the agents' doEvent method
			// in time step, iterate over ALL agents, ignore time of next event
			for (Agent agent : cel) agent.doEventSim(getSimulationTime());
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(cel);
			rebuildTaskList = BasicAgent.hasModelChangeRequest();
			BasicAgent.setSimulationIsRunning(false);
			callEventsProcessedHook();
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(getTimeStep()));
		}
	}

	public Time getTimeStep() { return timeStep; }

}
