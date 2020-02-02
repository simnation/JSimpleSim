/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.Message;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.IAgentState;

/**
 * Base class for agents providing common functionality and variables
 * <p>
 * This base class can be used for multi-domain models with a routed messaging system.
 * For routing, each agent has a unique address implemented as an {@code int[]} where the index corresponds 
 * to the level of the model tree and the value is a unique id within this level. 
 *
 * @param S type of the agent state containing all state variables
 * @param E type of events
 *
 */
public abstract class SimpleAgent<S extends IAgentState,E> extends AbstractAgent<S, E> {

	private final AbstractPort inport, outport;

	public SimpleAgent(S state, int[] addr) {
		super(state);
		setAddress(addr);
		inport=addSingleInport();
		outport=addSingleOutport();
	}

	@Override
	protected Time doEvent(Time time) {
		processMessages();
		processEvents(time);
		executeStrategy(time);
		return getTimeOfNextEvent();
	}

	/**
	 * Handles the content of a due message.
	 *
	 * @param msg the next message to be handled by the agent
	 */
	protected abstract void handleMessage(Message<int[]> msg);

	/**
	 * Handles a due event.
	 *
	 * @param <E>   the type of the event
	 * @param event the event as such (containing also additional information)
	 * @param time  the time stamp of the event
	 */
	protected abstract void handleEvent(E event, Time time);

	/**
	 * Executes the agent's strategy thus implements the agent's behavior
	 *
	 * @param time the current simulation time
	 */
	protected abstract void executeStrategy(Time time);

	/**
	 * Sends a message via the agent's outport.
	 *
	 * @param dest    the destination of the message
	 * @param content the content of the message
	 */
	protected final void sendMessage(int[] dest, Object content) {
		final Message<int[]> message=new Message<>(getAddress(),dest,content);
		getOutport().write(message);
	}

	protected final AbstractPort getInport() {
		return inport;
	}

	protected final AbstractPort getOutport() {
		return outport;
	}

	@SuppressWarnings("unchecked")
	private void processMessages() {
		while (getInport().hasMessages()) handleMessage(((Message<int[]>) (getInport().read())));
	}

	private void processEvents(Time time) {
		while (getEventQueue().getMin().equals(time)) handleEvent(getEventQueue().dequeue(),time);
	}

}
