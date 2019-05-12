/**
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
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.Collection;

import org.simplesim.model.AbstractAgent;

/**
 * Interface for various strategies to forward messages during simulation
 *
 */
public interface IMessageForwardingStrategy {

	/**
	 * Exception to be thrown if a port incompatible for the forwarding strategy is
	 * encountered.
	 */
	@SuppressWarnings("serial")
	static class PortTypeMismatchException extends RuntimeException {
		public PortTypeMismatchException(String message) {
			super(message);
		}
	}
	
	/**
	 * Dies the message forwarding.
	 * 
	 * @param sender collection of senders where the message originate
	 */
	void forwardMessages(Collection<AbstractAgent<?, ?>> sender);

	/**
	 * Build a collection of all ports with outgoing messages based on the collection of sending agents.
	 * 
	 * @param agentList collection of agents with outgoing messages
	 * @return collection of outports with outgoing messages to be processed
	 */
	default Collection<AbstractPort> listPortsWithOutgoingMsg(Collection<AbstractAgent<?, ?>> agentList) {
		final Collection<AbstractPort> result=new ArrayList<>();
		for (final AbstractAgent<?, ?> agent : agentList)
			for (final AbstractPort port : agent.getOutports()) if (port.hasMessages()) result.add(port);
		return result;
	}

}
