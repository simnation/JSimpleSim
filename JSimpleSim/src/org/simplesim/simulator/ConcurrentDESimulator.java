/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.BasicAgent;
import org.simplesim.model.BasicDomain;
import org.simplesim.model.Agent;

/**
 * Concurrent simulator for discrete event models using multiple threads
 * <p>
 * This simulator identifies all due agents of a model using a global event
 * queue. Then the {@code doEventSim} method of these imminent agents are called
 * in a concurrent mode and with no specific order.
 * <p>
 * This implementation is especially useful to run DES models.
 */
public final class ConcurrentDESimulator extends SequentialDESimulator {

	/**
	 * Constructs a new concurrent simulator with given model, queue implementation
	 * and messaging strategy
	 *
	 * @param root       the root domain of the model
	 * @param queue      the queue implementation to use as global event queue
	 * @param forwarding the strategy to use for message forwarding
	 */
	public ConcurrentDESimulator(BasicDomain root, EventQueue<Agent> queue, MessageForwardingStrategy forwarding) {
		super(root,queue,forwarding);
	}

	/**
	 * Quick start constructor of a new concurrent discrete-event simulator with a
	 * given model
	 * <p>
	 * Uses {@code RecursiveMessageForwarding} and a {@code HeapEventQueue} as
	 * default options.
	 *
	 * @param root the root domain of the model
	 */
	public ConcurrentDESimulator(BasicDomain root) {
		super(root);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.simplesim.simulator.SequentialDESimulator#runSimulation(org.simplesim.
	 * core.scheduling.Time)
	 */
	@Override
	public void runSimulation(Time stop) {
		initGlobalEventQueue();
		setSimulationTime(getGlobalEventQueue().getMin());
		// use a variable thread pool with as many worker threads as cpu cores
		final ExecutorService executor=Executors.newWorkStealingPool();
		final List<Future<Time>> futures=new ArrayList<>();
		while (getSimulationTime().compareTo(stop)<0) {
			BasicAgent.toggleSimulationIsRunning(true);
			// part I: process all current events by calling the agents' doEvent method
			// and enqueue the next events of the agents
			setCurrentEventList(getGlobalEventQueue().dequeueAll());
			// start multi-threaded execution
			for (Agent agent : getCurrentEventList())
				futures.add(executor.submit(() -> agent.doEventSim(getSimulationTime())));
			// join threads again and collect results
			for (int index=0; index<futures.size(); index++) try {
				final Agent agent=getCurrentEventList().get(index);
				final Time tone=futures.get(index).get();
				if (tone==null) throw new Simulator.InvalidSimulatorStateException(
						"Local event queue is empty in agent "+agent.getFullName());
				if (tone.compareTo(getSimulationTime())<0) throw new Simulator.InvalidSimulatorStateException(
						"Tone "+tone.toString()+" is before current simulation time "+getSimulationTime().toString()
								+" in agent "+agent.getFullName());
				getGlobalEventQueue().enqueue(agent,tone);
			} catch (InterruptedException|ExecutionException exception) {
				exception.printStackTrace();
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(getCurrentEventList());
			BasicAgent.toggleSimulationIsRunning(false);
			futures.clear(); // free futures again
			hookEventsProcessed();
			setSimulationTime(getGlobalEventQueue().getMin());
		}
		executor.shutdown();
	}

}
