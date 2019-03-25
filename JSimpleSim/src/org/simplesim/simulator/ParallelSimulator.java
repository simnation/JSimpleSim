/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.SortedListEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * parallel simulator using multiple threads to execute the agents' logic
 * 
 * @author Rene Kuhlemann
 *
 */
public final class ParallelSimulator extends SequentialSimulator {

	public ParallelSimulator(AbstractDomain<?> model, IEventQueue<AbstractAgent<?, ?>> queue) {
		super(model,queue);
	}

	public ParallelSimulator(AbstractDomain<?> model) {
		this(model,new SortedListEventQueue<AbstractAgent<?, ?>>());
	}

	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		final ExecutorService executor=Executors.newCachedThreadPool();
		final List<Future<Time>> futures=new ArrayList<>();
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		System.out.println("Simulation time is "+getSimulationTime().toString());
		while (getSimulationTime().getTicks()<stop.getTicks()) {
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			final List<AbstractAgent<?, ?>> agentList=getGlobalEventQueue().dequeueAll(getSimulationTime());
			// System.out.println("Number of concurrent events: "+current.size());
			// start multi-threaded execution
			for (final AbstractAgent<?, ?> agent : agentList)
				futures.add(executor.submit(() -> agent.doEventSim(getSimulationTime())));
			// join things again and collect results
			for (int index=0; index<agentList.size(); index++) try {
				getGlobalEventQueue().enqueue(agentList.get(index),futures.get(index).get());
			} catch (InterruptedException|ExecutionException exception) {
				exception.printStackTrace();
			}
			futures.clear(); // free Futures again
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(agentList);
			setSimulationTime(getGlobalEventQueue().getMin());
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		executor.shutdown();
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

}
