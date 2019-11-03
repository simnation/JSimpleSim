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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.simplesim.core.routing.IMessageForwardingStrategy;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * parallel simulator using multiple threads to execute the agents' logic
 *
 *
 */
public final class ConcurrentDESimulator extends SequentialDESimulator {

	public ConcurrentDESimulator(AbstractDomain model, IEventQueue<AbstractAgent<?, ?>> queue,
			IMessageForwardingStrategy forwarding) {
		super(model,queue,forwarding);
	}

	public ConcurrentDESimulator(AbstractDomain model) {
		super(model);
	}

	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		// used a variable thread pool with as many worker threads as cpu cores
		final ExecutorService executor=Executors.newWorkStealingPool();
		final List<Future<Time>> futures=new ArrayList<>();
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			setCurrentEventList(getGlobalEventQueue().dequeueAll());
			// System.out.println("Number of concurrent events: "+current.size());
			// start multi-threaded execution
			for (final AbstractAgent<?, ?> agent : getCurrentEventList())
				futures.add(executor.submit(() -> agent.doEventSim(getSimulationTime())));
			// join things again and collect results
			for (int index=0; index<getCurrentEventList().size(); index++) try {
				getGlobalEventQueue().enqueue(getCurrentEventList().get(index),futures.get(index).get());
			} catch (InterruptedException|ExecutionException exception) {
				exception.printStackTrace();
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			hookEventsProcessed();
			futures.clear(); // free Futures again
			setSimulationTime(getGlobalEventQueue().getMin());
		}
		executor.shutdown();
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

}
