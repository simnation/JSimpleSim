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

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 *  To be documented
 *
 */
public final class TimeStepSimulator implements ISimulator {

	// top node of the simulation model
	private final AbstractDomain<?> root;

	// current simulation time
	private Time simTime;

	// the constant time step, no event queue
	private final Time timeStep;

	// the message forwarding strategy
	private final IMessageForwardingStrategy mfs;

	public TimeStepSimulator(AbstractDomain<?> rt, Time step, IMessageForwardingStrategy forwarding) {
		root=rt;
		timeStep=step;
		mfs=forwarding;
	}
	
	public TimeStepSimulator(AbstractDomain<?> root, IMessageForwardingStrategy forwarding) {
		this(root,new Time(Time.MINUTE),forwarding);
	}

	public TimeStepSimulator(AbstractDomain<?> root) {
		this(root,new Time(Time.MINUTE),new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		setSimulationTime(Time.getZero());
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// in time step, iterate over ALL agents, ignore time of next event
			for (final AbstractAgent<?, ?> agent : getRoot().listAllAgents()) agent.doEventSim(getSimulationTime());
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getRoot().listAllAgents());
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(timeStep));
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

	@Override
	public Time getSimulationTime() {
		return simTime;
	}

	private void setSimulationTime(Time time) {
		simTime=time;
	}

	private AbstractDomain<?> getRoot() {
		return root;
	}

	/**
	 * @return the mfs
	 */
	private IMessageForwardingStrategy getMessageForwardingStrategy() {
		return mfs;
	}

}
