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

import org.simplesim.core.messaging.AbstractPort;

/**
 * Strategy for direct message forwarding.
 * <p>
 * Connects agents <i>directly</i>, without taking care of a model hierarchy.
 * <p>
 * Use this implementation of a {@code ForwardingStrategy} for graphs,
 * intermeshed networks and when there is no need for a model hierarchy.
 *
 */
public final class DirectMessageForwarding implements MessageForwardingStrategy {

	@Override
	public void forwardMessages(Collection<Agent> agentList) {
		// part I: get list of all ports carrying an outgoing message
		final Collection<AbstractPort> destinations=new ArrayList<>();
		final Collection<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		// part II: do forwarding of messages, only one Step because of the direct
		// connections
		for (final AbstractPort src : sources) destinations.addAll(src.forwardMessages());
		// Only one copy cycle because there should be only direct connections
	}

}
