/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.instrumentation.ListenerSupport;
import org.simplesim.core.scheduling.Time;

/**
 * Decorator class to enable instrumentation of specific agents during
 * simulation run.
 * <p>
 * This class wraps an {@code Agent} implementation and notifies any registered
 * listeners. Notification can occur <i>before</i> or <i>after</i> the agent's
 * {@code doEvent} method is called, which renders its strategy.
 * <p>
 * Querying of an agent can be done by analyzing the agent's state (using
 * {@code getState}). A unique id should be provided by {@code getFullName}. The
 * current time-stamp is passed as parameter to the listener.
 * <p>
 * <b>Do not change the agent's state nor call any other methods since this may
 * result in erratic behavior of the simulation!</b>
 *
 * @see Listener
 * @see Agent
 */
public class InstrumentationDecorator implements Agent {

	/* the encapsulated simulator */
	private final Agent agent;

	// listeners to notify BEFORE agent is called
	private final ListenerSupport<Agent> beforeExecutionListeners=new ListenerSupport<>();

	// listeners to notify AFTER agent is called
	private final ListenerSupport<Agent> afterExecutionListeners=new ListenerSupport<>();

	public InstrumentationDecorator(Agent value) {
		agent=value;
	}

	public void registerBeforeExecutionListener(Listener<Agent> listener) {
		beforeExecutionListeners.registerListener(listener);
	}

	public void unregisterBeforeExecutionListener(Listener<Agent> listener) {
		beforeExecutionListeners.unregisterListener(listener);
	}

	public void registerAfterExecutionListener(Listener<Agent> listener) {
		afterExecutionListeners.registerListener(listener);
	}

	public void unregisterAfterExecutionListener(Listener<Agent> listener) {
		afterExecutionListeners.unregisterListener(listener);
	}

	@Override
	public Time doEvent(Time time) {
		beforeExecutionListeners.notifyListeners(time,this);
		agent.doEvent(time);
		afterExecutionListeners.notifyListeners(time,this);
		return getTimeOfNextEvent();
	}

	@Override
	public <S extends State> S getState() { return agent.getState(); }

	@Override
	public Time getTimeOfNextEvent() { return agent.getTimeOfNextEvent(); }

	@Override
	public String getName() { return agent.getName(); }

	@Override
	public String getFullName() { return agent.getFullName(); }

	@Override
	public boolean hasInput() { return agent.hasInput(); }

	@Override
	public int[] getAddress() { return agent.getAddress(); }

}
