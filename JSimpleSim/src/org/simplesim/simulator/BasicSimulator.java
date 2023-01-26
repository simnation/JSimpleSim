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

import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.instrumentation.ListenerSupport;
import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.BasicDomain;
import org.simplesim.model.Domain;
import org.simplesim.model.Agent;

/**
 * Implements the core functionality of a simulator.
 *
 */
public abstract class BasicSimulator implements Simulator {

	// top node of the simulation model
	private final Domain rootDomain;

	// current simulation time
	private Time simTime=Time.ZERO;

	// the global event queue of the simulation
	private final EventQueue<Agent> geq;

	// the strategy used to forward messages during a simulation run
	private final MessageForwardingStrategy mfs;

	// listeners to notify after all agents of a cycle have been processed
	private final ListenerSupport<Simulator> eventsProcessedListeners=new ListenerSupport<>();

	// list of agents processed in current simulation cycle
	private List<Agent> currentEventList;

	/**
	 * Constructs a new simulator with given model, queue implementation and
	 * messaging strategy
	 *
	 * @param root       the root domain of the model
	 * @param queue      the queue implementation to use as global event queue
	 * @param forwarding the strategy to use for message forwarding
	 */
	public BasicSimulator(BasicDomain root, EventQueue<Agent> queue, MessageForwardingStrategy forwarding) {
		rootDomain=root;
		geq=queue;
		mfs=forwarding;
	}

	/**
	 * Starts a simulation run
	 *
	 * @param stop simulation time when the simulation should stop
	 *
	 * @exception Simulator.InvalidSimulatorStateException if there is an error during
	 *                                           simulation
	 */
	@Override
	public abstract void runSimulation(Time stop);

	/**
	 * Builds the global event queue by querying the local event queues of all
	 * agents within the root model
	 */
	protected void initGlobalEventQueue() {
		for (Agent agent : getRootDomain().listAllAgents(true)) {
			final Time tone=agent.getTimeOfNextEvent();
			if (tone==null)
				throw new Simulator.InvalidSimulatorStateException("Local event queue empty in agent "+agent.getFullName());
			getGlobalEventQueue().enqueue(agent,tone);
		}
	}

	@Override
	public Domain getRootDomain() { return rootDomain; }

	@Override
	public Time getSimulationTime() { return simTime; }

	protected void setSimulationTime(Time time) { simTime=time; }

	@Override
	public void registerEventsProcessedListener(Listener<Simulator> listener) {
		eventsProcessedListeners.registerListener(listener);
	}

	@Override
	public void unregisterEventsProcessedListener(Listener<Simulator> listener) {
		eventsProcessedListeners.unregisterListener(listener);
	}

	protected void hookEventsProcessed() {
		eventsProcessedListeners.notifyListeners(this);
	}

	protected MessageForwardingStrategy getMessageForwardingStrategy() { return mfs; }

	protected EventQueue<Agent> getGlobalEventQueue() { return geq; }

	@Override
	public List<Agent> getCurrentEventList() { return currentEventList; }

	protected void setCurrentEventList(List<Agent> list) { currentEventList=list; }

}
