/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.Collection;

import org.simplesim.core.messaging.Port;

/**
 * Interface for various strategies to forward messages during simulation
 *
 */
public interface MessageForwardingStrategy {

	/**
	 * Exception to be thrown if there is an error in the process of message
	 * forwarding
	 *
	 */
	@SuppressWarnings("serial")
	static final class ForwardingFailureException extends RuntimeException {
		public ForwardingFailureException(String message) {
			super(message);
		}
	}

	/**
	 * Dies the message forwarding.
	 *
	 * @param sender collection of senders where the message originate
	 */
	void forwardMessages(Collection<Agent> sender);

	/**
	 * Build a collection of all ports with outgoing messages based on the
	 * collection of sending agents.
	 *
	 * @param agentList collection of agents with outgoing messages
	 * @return collection of outports with outgoing messages to be processed
	 */
	default Collection<Port> listPortsWithOutgoingMsg(Collection<Agent> agentList) {
		final Collection<Port> result=new ArrayList<>();
		for (final Agent agent : agentList) {
			Port port=agent.getOutport();
			if (port.hasMessages()) result.add(port);
		}
		return result;
	}

}
