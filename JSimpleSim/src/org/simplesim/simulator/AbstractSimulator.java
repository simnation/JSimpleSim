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

import java.util.Collections;
import java.util.List;

import org.simplesim.core.notification.Listener;
import org.simplesim.core.notification.ListenerSupport;
import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;

/**
 * Implements the core functionality of a simulator.
 *
 */
public abstract class AbstractSimulator {

	// top node of the simulation model
	private final AbstractDomain root;

	// current simulation time
	private Time simTime=Time.ZERO;

	// the global event queue of the simulation
	private final IEventQueue<AbstractAgent<?, ?>> geq;

	// the strategy used to forward messages during a simulation run
	private final IMessageForwardingStrategy mfs;

	// list of all agents processed in the last event cycle
	private List<AbstractAgent<?, ?>> currentEventList=Collections.emptyList();

	// listeners to notify after all agents of a cycle have been processed
	private final ListenerSupport<AbstractSimulator> eventsProcessedListeners=new ListenerSupport<>();

	/**
	 *
	 * @param rt
	 * @param queue
	 * @param forwarding
	 */
	public AbstractSimulator(AbstractDomain rt, IEventQueue<AbstractAgent<?, ?>> queue,
			IMessageForwardingStrategy forwarding) {
		root=rt;
		geq=queue;
		mfs=forwarding;
	}

	/**
	 * Starts a simulation run.
	 *
	 * @param stop simulation time when the simulation should stop
	 *
	 * @exception NullPointerException if the event queue is empty before the stop
	 *                                 time is reached
	 */
	public abstract void runSimulation(Time stop);
	
	/**
	 * Builds the global event queue by querying the local event queues of all agents within the root model
	 */
	protected void initGlobalEventQueue() {
		for (final AbstractAgent<?, ?> agent : getRoot().listAllAgents(true)) {
			final Time tone=agent.getTimeOfNextEvent();
			if (tone==null) throw new NullPointerException("Local event queue empty in agent "+agent.getFullName());
			getGlobalEventQueue().enqueue(agent,tone);
		}
	}

	public AbstractDomain getRoot() {
		return root;
	}

	public Time getSimulationTime() {
		return simTime;
	}

	protected void setSimulationTime(Time time) {
		simTime=time;
	}

	public List<AbstractAgent<?, ?>> getCurrentEventList() {
		return currentEventList;
	}

	protected void setCurrentEventList(List<AbstractAgent<?, ?>> list) {
		currentEventList=list;
	}

	public void registerEventsProcessedListener(Listener<AbstractSimulator> listener) {
		eventsProcessedListeners.registerListener(listener);
	}

	public void unregisterEventsProcessedListener(Listener<AbstractSimulator> listener) {
		eventsProcessedListeners.unregisterListener(listener);
	}

	/**
	 * 
	 */
	protected void hookEventsProcessed() {
		eventsProcessedListeners.notifyListeners(this);
	}

	protected IMessageForwardingStrategy getMessageForwardingStrategy() {
		return mfs;
	}

	protected IEventQueue<AbstractAgent<?, ?>> getGlobalEventQueue() {
		return geq;
	}

}
