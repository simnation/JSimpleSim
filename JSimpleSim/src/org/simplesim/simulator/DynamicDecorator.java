/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.simulator;

import java.util.List;

import org.simplesim.core.dynamic.ChangeRequest;
import org.simplesim.core.notification.Listener;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;

/**
 * Decorator class to enable dynamic changes of the model during simulation run.
 * <p>
 * This class wraps any other simulator implementation and registers itself as {@code EventsProcessedListener} to be
 * called after each simulation cycle. Communication of the agents' change requests is done by a static and thread-safe
 * queue within the {@code DynamicAgent} class. Changes are done by concrete implementations of the {@code ChangeRequest}
 * interface. So, agents prepare and organize the change process whereas the the various change request implementations
 * are responsible for the conduct of the specified model change. This class ensures the processing of the change requests.
 * <p>
 * To use model change functionality, active agents must be derived from {@code DynamicAgent} class.
 *
 * @see ChangeRequest
 * @see DynamicAgent
 */
public class DynamicDecorator implements ISimulator {

	/* the encapsulated simulator */
	private final AbstractSimulator simulator;

	public DynamicDecorator(AbstractSimulator value) {
		simulator=value;
		simulator.registerEventsProcessedListener((sim) -> doModelChanges());
	}

	/**
	 * Goes through the queue of model change requests and calls the concrete implementations
	 */
	private void doModelChanges() {
		ChangeRequest cr=AbstractAgent.pollModelChangeRequest();
		while (cr!=null) {
			cr.doModelChange();
			cr=AbstractAgent.pollModelChangeRequest();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#runSimulation(org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void runSimulation(Time stop) {
		simulator.runSimulation(stop);
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#getRootDomain()
	 */
	@Override
	public AbstractDomain getRootDomain() {
		return simulator.getRootDomain();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#getSimulationTime()
	 */
	@Override
	public Time getSimulationTime() {
		return simulator.getSimulationTime();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#getCurrentEventList()
	 */
	@Override
	public List<AbstractAgent<?, ?>> getCurrentEventList() {
		return simulator.getCurrentEventList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.simulator.ISimulator#registerEventsProcessedListener(org.simplesim.core.notification.Listener)
	 */
	@Override
	public void registerEventsProcessedListener(Listener<AbstractSimulator> listener) {
		simulator.registerEventsProcessedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.simplesim.simulator.ISimulator#unregisterEventsProcessedListener(org.simplesim.core.notification.Listener)
	 */
	@Override
	public void unregisterEventsProcessedListener(Listener<AbstractSimulator> listener) {
		simulator.unregisterEventsProcessedListener(listener);
	}

}
