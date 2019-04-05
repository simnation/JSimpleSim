/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.simulator;

import java.util.List;

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;
import org.simplesim.model.RootModel;

/**
 * Simulator to run a {@link RootModel} by processing events sequentially
 *
 * @author Rene Kuhlemann
 *
 */
public class SequentialSimulator {

	// top node of the simulation model
	private final AbstractDomain<?> root;

	// current simulation time
	private Time simTime;

	// the global event queue of the simulation
	private final IEventQueue<AbstractAgent<?, ?>> geq;

	private IMessageForwardingStrategy mfs=new RecursiveMessageForwarding();

	public SequentialSimulator(AbstractDomain<?> rt, IEventQueue<AbstractAgent<?, ?>> queue) {
		root=rt;
		geq=queue;
	}

	public SequentialSimulator(AbstractDomain<?> root) {
		this(root,new SortedEventQueue<AbstractAgent<?, ?>>());
	}

	void initGlobalEventQueue() {
		for (final AbstractAgent<?, ?> iter : getRoot().getAllAgents()) {
			final Time tone=iter.getTimeOfNextEvent();
			if (tone==null) throw new NullPointerException("Local event queue empty in agent "+iter.getFullName());
			getGlobalEventQueue().enqueue(iter,tone);
		}
	}

	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		System.out.println("Simulation time is "+getSimulationTime().toString());

		while (getSimulationTime().getTicks()<stop.getTicks()) {
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			final List<AbstractAgent<?, ?>> eventList=getGlobalEventQueue().dequeueAll(getSimulationTime());
			// System.out.println("Number of concurrent events: "+list.size());

			for (final AbstractAgent<?, ?> entry : eventList) {
				final Time tonie=entry.doEventSim(getSimulationTime());
				if (tonie==null)
					throw new NullPointerException("Local event queue empty in agent "+entry.getFullName());
				getGlobalEventQueue().enqueue(entry,tonie);
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(eventList);
			setSimulationTime(getGlobalEventQueue().getMin());
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

	void setSimulationTime(Time time) {
		simTime=time;
	}

	AbstractDomain<?> getRoot() {
		return root;
	}

	IEventQueue<AbstractAgent<?, ?>> getGlobalEventQueue() {
		return geq;
	}

	public Time getSimulationTime() {
		return simTime;
	}

	/**
	 * @return the mfs
	 */
	public IMessageForwardingStrategy getMessageForwardingStrategy() {
		return mfs;
	}

	/**
	 * @param mfs the mfs to set
	 */
	public void setMessageForwardingStrategy(IMessageForwardingStrategy mfs) {
		this.mfs=mfs;
	}

}
