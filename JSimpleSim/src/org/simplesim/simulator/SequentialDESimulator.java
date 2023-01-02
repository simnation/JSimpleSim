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

import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.Agent;
import org.simplesim.model.MessageForwardingStrategy;
import org.simplesim.model.RecursiveMessageForwarding;

/**
 * Sequential simulator for discrete event models using a single threads
 * <p>
 * This simulator identifies all due agents of a model using a global event
 * queue. Then the {@code doEventSim} method of these imminent agents is called
 * sequentially.
 * <p>
 * Uses a {@code SortedBucketQueue} as default implementation of the global
 * event queue
 * <p>
 * This implementation is especially useful to run DES models.
 *
 */
public class SequentialDESimulator extends AbstractSimulator {

	/**
	 * Constructs a new sequential simulator with given model, queue implementation
	 * and messaging strategy
	 *
	 * @param root       the root domain of the model
	 * @param queue      the queue implementation to use as global event queue
	 * @param forwarding the strategy to use for message forwarding
	 */
	public SequentialDESimulator(AbstractDomain root, EventQueue<Agent> queue, MessageForwardingStrategy forwarding) {
		super(root,queue,forwarding);
	}

	/**
	 * Quick start constructor of a sequential discrete-event simulator with a given model
	 * <p>
	 * Uses {@code RecursiveMessageForwarding} and a {@code HeapEventQueue} as
	 * default options.
	 *
	 * @param root the root domain of the model
	 */
	public SequentialDESimulator(AbstractDomain root) {
		this(root,new HeapEventQueue<Agent>(),new RecursiveMessageForwarding());
	}

	public SequentialDESimulator(AbstractDomain root, EventQueue<Agent> queue) {
		this(root,queue,new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		while (getSimulationTime().compareTo(stop)<0) {
			AbstractAgent.toggleSimulationIsRunning(true);
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			setCurrentEventList(getGlobalEventQueue().dequeueAll());
			// System.out.println("Number of concurrent events: "+list.size());
			for (Agent agent : getCurrentEventList()) {
				final Time tone=agent.doEvent(getSimulationTime());
				if (tone==null) throw new InvalidSimulatorStateException(
						"Local event queue is empty in agent "+agent.getFullName());
				if (tone.compareTo(getSimulationTime())<0) throw new InvalidSimulatorStateException(
						"Tonie "+tone.toString()+" is before current simulation time "+getSimulationTime().toString()
								+" in agent "+agent.getFullName());
				getGlobalEventQueue().enqueue(agent,tone);
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			AbstractAgent.toggleSimulationIsRunning(false);
			hookEventsProcessed();
			setSimulationTime(getGlobalEventQueue().getMin());
		}
	}

}
