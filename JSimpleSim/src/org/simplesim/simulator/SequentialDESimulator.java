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
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * Simulator to run a model by sequential event processing.
 *
 */
public class SequentialDESimulator extends AbstractSimulator {

	public SequentialDESimulator(AbstractDomain root, IEventQueue<AbstractAgent<?, ?>> queue,
			IMessageForwardingStrategy forwarding) {
		super(root,queue,forwarding);
	}

	public SequentialDESimulator(AbstractDomain root) {
		this(root,new SortedEventQueue<AbstractAgent<?, ?>>(),new RecursiveMessageForwarding());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.simulator.ISimulator#runSimulation(org.simplesim.core.
	 * scheduling.Time)
	 */
	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			setCurrentEventList(getGlobalEventQueue().dequeueAll());
			// System.out.println("Number of concurrent events: "+list.size());
			for (final AbstractAgent<?, ?> entry : getCurrentEventList()) {
				final Time tonie=entry.doEventSim(getSimulationTime());
				if (tonie==null)
					throw new NullPointerException("Local event queue empty in agent "+entry.getFullName());
				getGlobalEventQueue().enqueue(entry,tonie);
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			hookEventsProcessed();
			setSimulationTime(getGlobalEventQueue().getMin());
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

}
