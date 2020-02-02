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

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * Concurrent simulator for discrete event models using multiple threads 
 * <p>
 * This simulator identifies all due agents of a model using a global event queue.
 * Then the {@code doEventSim} method of these imminent agents is called sequentially.
 * <p>
 * This implementation is especially useful to run DES models.
 *
 */
public class SequentialDESimulator extends AbstractSimulator {
	
	/**
	 * Constructs a new sequential simulator with given model, queue implementation and messaging strategy
	 *
	 * @param root the root domain of the model
	 * @param queue the queue implementation to use as global event queue
	 * @param forwarding the strategy to use for message forwarding
	 */
	public SequentialDESimulator(AbstractDomain root, IEventQueue<AbstractAgent<?, ?>> queue,
			IMessageForwardingStrategy forwarding) {
		super(root,queue,forwarding);
	}

	public SequentialDESimulator(AbstractDomain root) {
		this(root,new HeapEventQueue<AbstractAgent<?, ?>>(),new RecursiveMessageForwarding());
	}
	
	public SequentialDESimulator(AbstractDomain root, IMessageForwardingStrategy forwarding) {
		this(root,new HeapEventQueue<AbstractAgent<?, ?>>(),forwarding);
	}
	
	public SequentialDESimulator(AbstractDomain root, IEventQueue<AbstractAgent<?, ?>> queue) {
		this(root,queue,new RecursiveMessageForwarding());
	}


	/* (non-Javadoc)
	 * @see org.simplesim.simulator.AbstractSimulator#runSimulation(org.simplesim.core.scheduling.Time)
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
			for (final AbstractAgent<?, ?> agent : getCurrentEventList()) {
				final Time tonie=agent.doEventSim(getSimulationTime());
				if (tonie==null) throw new InvalidSimulatorStateException(
						"Local event queue is empty in agent "+agent.getFullName());
				if (tonie.compareTo(getSimulationTime())<0) throw new InvalidSimulatorStateException(
						"Tonie "+tonie.toString()+" is before current simulation time "+
						getSimulationTime().toString()+" in agent "+agent.getFullName());
				getGlobalEventQueue().enqueue(agent,tonie);
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
