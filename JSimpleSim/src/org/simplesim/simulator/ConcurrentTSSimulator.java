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
import org.simplesim.core.routing.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * Simulator for concurrent time step simulation
 * <p>
 * This simulator calls all agents of a model at equidistant time steps. The {@code doEventSim} method
 * of the agents is called in a concurrent mode and with no specific oder every {@code timeStep}.
 * <p>
 * This implementation is especially useful to run cellular automata.
 *
 */
public final class ConcurrentTSSimulator extends SequentialTSSimulator {

	public ConcurrentTSSimulator(AbstractDomain rt, Time step, IMessageForwardingStrategy forwarding) {
		super(rt,step,forwarding);
	}

	public ConcurrentTSSimulator(AbstractDomain root, IMessageForwardingStrategy forwarding) {
		this(root,new Time(Time.MINUTE),forwarding);
	}

	public ConcurrentTSSimulator(AbstractDomain root) {
		this(root,new Time(Time.MINUTE),new RecursiveMessageForwarding());
	}

	@Override
	public void runSimulation(Time stop) {
		BasicModelEntity.toggleSimulationIsRunning(true);
		final List<AbstractAgent<?, ?>> currentEventList=getRootDomain().listAllAgents(true);
		// used a variable thread pool with a maximum of as many worker threads as cpu
		// cores		
		final ExecutorService executor=Executors.newWorkStealingPool();
		final List<Future<?>> futures=new ArrayList<>();
		setSimulationTime(Time.ZERO);
		while (getSimulationTime().compareTo(stop)<0) {
			// part I: process all current events by calling the agents' doEvent method
			// in time step, iterate over ALL agents, ignore time of next event
			for (final AbstractAgent<?, ?> agent : currentEventList)
				futures.add(executor.submit(() -> agent.doEventSim(getSimulationTime())));
			// wait until all threads have finished
			try {
				for (final Future<?> item : futures) item.get();
			} catch (InterruptedException|ExecutionException exception) {
				exception.printStackTrace();
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(currentEventList);
			futures.clear();
			hookEventsProcessed();
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(getTimeStep()));
			// System.out.println("Simulation time is "+getSimulationTime().toString());
		}
		executor.shutdown();
		BasicModelEntity.toggleSimulationIsRunning(false);
	}

}
