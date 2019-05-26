/**
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

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * To be documented
 *
 */
public class SequentialTSSimulator extends AbstractSimulator {

	// the constant time step, no event queue
	private final Time timeStep;

	public SequentialTSSimulator(AbstractDomain rt, Time step, IMessageForwardingStrategy forwarding) {
		super(rt,null,forwarding);
		timeStep=step;
	}

	public SequentialTSSimulator(AbstractDomain root, IMessageForwardingStrategy forwarding) {
		this(root,new Time(Time.MINUTE),forwarding);
	}

	public SequentialTSSimulator(AbstractDomain root) {
		this(root,new Time(Time.MINUTE),new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		setCurrentEventList(getRoot().listAllAgents(true));
		setSimulationTime(Time.getZero());
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// in time step, iterate over ALL agents, ignore time of next event
			for (final AbstractAgent<?, ?> agent : getCurrentEventList()) agent.doEventSim(getSimulationTime());
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			hookEventsProcessed();
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(getTimeStep()));
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

	public Time getTimeStep() {
		return timeStep;
	}

}