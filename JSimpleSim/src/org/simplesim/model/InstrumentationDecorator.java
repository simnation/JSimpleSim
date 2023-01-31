/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import org.simplesim.core.dynamic.ChangeDomainRequest;
import org.simplesim.core.dynamic.RemoveEntityRequest;
import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.instrumentation.ListenerSupport;
import org.simplesim.core.messaging.Port;
import org.simplesim.core.scheduling.EventQueue;
import org.simplesim.core.scheduling.Time;

/**
 * Decorator to add instrumentation functionality to an agent.
 * <p>
 * The decorator adds listeners to the {@code doEvent} method, so that the
 * agent's state can be queried before and after the execution of the agent's
 * strategy. This enables snapshots for data time series and debugging.
 * <p>
 * The agent's state should considered as <u>read only</u>.
 *
 */
public final class InstrumentationDecorator<S extends State, E> extends BasicAgent<S, E> {

	/** the encapsulated agent */
	private final BasicAgent<S, E> agent;

	/** before execution listener */
	private final ListenerSupport<BasicAgent<?, ?>> bel=new ListenerSupport<>();

	/** after execution listener */
	private final ListenerSupport<BasicAgent<?, ?>> ael=new ListenerSupport<>();

	public InstrumentationDecorator(BasicAgent<S, E> who) {
		super(null,null); // the decorator has no state, no event queue
		agent=who;
		agent.setDomainChangeStrategy(new DomainChangeStrategy() {

			@Override
			public void sendDomainChangeRequest(BasicDomain dest) {
				pushModelChangeRequest(new ChangeDomainRequest(InstrumentationDecorator.this,dest));
			}

			@Override
			public void sendEntityRemoveRequest() {
				pushModelChangeRequest(new RemoveEntityRequest(InstrumentationDecorator.this));
			}

		});
	}

	public void registerBeforeExecutionListener(Listener<BasicAgent<?, ?>> listener) {
		bel.registerListener(listener);
	}

	public void unregisterBeforeExecutionListener(Listener<BasicAgent<?, ?>> listener) {
		bel.unregisterListener(listener);
	}

	public void registerAfterExecutionListener(Listener<BasicAgent<?, ?>> listener) {
		ael.registerListener(listener);
	}

	public void unregisterAfterExecutionListener(Listener<BasicAgent<?, ?>> listener) {
		ael.unregisterListener(listener);
	}

	@Override
	public void addToDomain(BasicDomain domain) {
		domain.addEntity(this);
	}

	@Override
	public void removeFromDomain() {
		((BasicDomain) getParent()).removeEntity(this);
	}

	@Override
	public Time doEvent(Time time) {
		bel.notifyListeners(time,this);
		agent.doEvent(time);
		ael.notifyListeners(time,this);
		return getTimeOfNextEvent();
	}

	@Override
	public S getState() { return agent.getState(); }

	@Override
	public Time getTimeOfNextEvent() { return agent.getTimeOfNextEvent(); }

	@Override
	public String getName() { return agent.getName(); }

	@Override
	public String getFullName() { return agent.getFullName(); }

	@Override
	public Domain getParent() { return agent.getParent(); }

	@Override
	public int[] getAddress() { return agent.getAddress(); }

	@Override
	public Port getInport() { return agent.getInport(); }

	@Override
	public Port getOutport() { return agent.getOutport(); }

	@Override
	public int getLevel() { return agent.getLevel(); }

	@Override
	protected EventQueue<E> getEventQueue() { return agent.getEventQueue(); }

	@Override
	protected Port setInport(Port port) { return agent.setInport(port); }

	@Override
	protected Port setOutport(Port port) { return agent.setOutport(port); }

	@Override
	void setAddress(int[] addr) { agent.setAddress(addr); }

	@Override
	void resetAddress(int index) { agent.resetAddress(index); }

	@Override
	void setParent(Domain par) { agent.setParent(par); }

}
