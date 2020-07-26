/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples;

import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.RoutingAgent;
import org.simplesim.model.State;

/**
 * Example of a simple implementation of an {@code RoutingAgent}
 */
public class SimpleAgent extends RoutingAgent<SimpleAgent.SimpleAgentState, SimpleAgent.EVENT> {

	enum EVENT {
		event1, event2, event3
	}

	static class SimpleAgentState implements State {
		/*
		 * Place state variables, getters and setters here.
		 */
	}

	public SimpleAgent() {
		super(new SimpleAgentState());
		/*
		 * Do some other initialization here.
		 */
	}

	@Override
	protected Time doEvent(Time time) {
		while (getInport().hasMessages()) handleMessage(((RoutedMessage) getInport().poll()));
		while (getEventQueue().getMin().equals(time)) handleEvent(getEventQueue().dequeue(),time);
		/*
		 * Do not forget to enqueue some new events.
		 */
		return getTimeOfNextEvent();
	}

	private void sendMessage(RoutingAgent<?,?> destination, Object content) {
		RoutedMessage message=new RoutedMessage(this.getAddress(),destination.getAddress(),content);
		getOutport().write(message);
	}

	private void handleMessage(RoutedMessage message) {
		/*
		 * Do the message handling here.
		 */
	}

	private void handleEvent(EVENT event, Time time) {
		/*
		 * Do the event handling here.
		 */
	}

}
