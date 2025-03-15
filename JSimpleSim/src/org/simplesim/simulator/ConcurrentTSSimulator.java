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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.Agent;
import org.simplesim.model.BasicAgent;
import org.simplesim.model.BasicDomain;

/**
 * Simulator for concurrent time step simulation
 * <p>
 * This simulator calls all agents of a model at equidistant time steps. The
 * {@code doEventSim} method of the agents is called in a concurrent mode and
 * with no specific oder every {@code timeStep}.
 * <p>
 * This implementation is especially useful to run cellular automata.
 *
 */
public final class ConcurrentTSSimulator extends SequentialTSSimulator {

	public ConcurrentTSSimulator(BasicDomain root, MessageForwardingStrategy forwarding) {
		super(root, forwarding);
	}

	/**
	 * Quick start constructor of a concurrent time-step simulator with a given
	 * model
	 * <p>
	 * Uses {@code RecursiveMessageForwarding} as default option.
	 *
	 * @param root the root domain of the model
	 */
	public ConcurrentTSSimulator(BasicDomain root) {
		super(root);
	}

	@Override
	public void runSimulation(Time stop) {
		setSimulationTime(Time.ZERO);
		// used a variable thread pool with a maximum of as many worker threads as cpu
		// cores
		final ExecutorService executor = Executors.newWorkStealingPool();
		final List<Callable<?>> tasks = new ArrayList<>();
		List<Agent> cel = Collections.emptyList(); // cel=current event list
		boolean rebuildTaskList = true;

		while (getSimulationTime().compareTo(stop) < 0) {
			BasicAgent.setSimulationIsRunning(true);
			// part 0: costly rebuild of list only if there are changes to the model
			if (rebuildTaskList) {
				tasks.clear();
				cel = getRootDomain().listAllAgents(true);
				for (Agent agent : cel)
					tasks.add(() -> agent.doEventSim(getSimulationTime()));
				rebuildTaskList = false;
			}
			// part I: invoke all agents
			try {
				executor.invokeAll(tasks);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			// part II: do the message forwarding
			getMessageForwardingStrategy().forwardMessages(cel);
			rebuildTaskList = BasicAgent.hasModelChangeRequest();
			BasicAgent.setSimulationIsRunning(false);
			callEventsProcessedHook();
			// part III: add the time step
			setSimulationTime(getSimulationTime().add(getTimeStep()));
		}
		executor.shutdown();
	}

}
