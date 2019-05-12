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

import java.util.List;

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;
import org.simplesim.testing.model.RootModel;

/**
 * Simulator to run a model by sequential event processing.
 *
 */
public class SequentialSimulator implements ISimulator {

	// top node of the simulation model
	private final AbstractDomain<?> root;

	// current simulation time
	private Time simTime;

	// the global event queue of the simulation
	private final IEventQueue<AbstractAgent<?, ?>> geq;

	private final IMessageForwardingStrategy mfs;

	public SequentialSimulator(AbstractDomain<?> rt, IEventQueue<AbstractAgent<?, ?>> queue, IMessageForwardingStrategy forwarding) {
		root=rt;
		geq=queue;
		mfs=forwarding;
	}

	public SequentialSimulator(AbstractDomain<?> root) {
		this(root,new SortedEventQueue<AbstractAgent<?, ?>>(),new RecursiveMessageForwarding());
	}

	void initGlobalEventQueue() {
		for (final AbstractAgent<?, ?> agent : root.listAllAgents()) {
			final Time tone=agent.getTimeOfNextEvent();
			if (tone==null) throw new NullPointerException("Local event queue empty in agent "+agent.getFullName());
			getGlobalEventQueue().enqueue(agent,tone);
		}
	}

	/* (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#runSimulation(org.simplesim.core.scheduling.Time)
	 */
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			final List<AbstractAgent<?, ?>> eventList=getGlobalEventQueue().dequeueAll();
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
	
	IMessageForwardingStrategy getMessageForwardingStrategy() {
		return mfs;
	}

	IEventQueue<AbstractAgent<?, ?>> getGlobalEventQueue() {
		return geq;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#getSimulationTime()
	 */
	public Time getSimulationTime() {
		return simTime;
	}

}
