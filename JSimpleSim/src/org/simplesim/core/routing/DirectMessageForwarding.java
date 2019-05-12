/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.Collection;

import org.simplesim.model.AbstractAgent;

/**
 * Strategy for direct message forwarding.
 * <p>
 * Connects agents <i>directly</i>, without taking care of a model hierarchy.
 * <p>
 * Use this implementation of a {@code IMessageForwardingStrategy} for graphs,
 * intermeshed networks and when there is no need for a model hierarchy.
 *
 */
public final class DirectMessageForwarding implements IMessageForwardingStrategy {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.devs.core.ports.IMessageForwardingStrategy#forwardMessages(java.util.
	 * List)
	 */
	@Override
	public void forwardMessages(Collection<AbstractAgent<?, ?>> agentList) {
		// part I: get list of all ports carrying an outgoing message
		final Collection<AbstractPort> destinations=new ArrayList<>();
		final Collection<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		// part II: do forwarding of messages, only one Step because of the direct
		// connections
		for (final AbstractPort src : sources) destinations.addAll(src.copyMessages());
		// Only one copy cycle because there should be only direct connections
	}

}
