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
package org.simplesim.core.messaging;

import java.util.ArrayList;
import java.util.Collection;

import org.simplesim.model.Agent;

/**
 * Strategy for direct message forwarding.
 * <p>
 * Forwards messages along <i>exactly one</i> connection. Use this implementation 
 * for graphs, intermeshed networks and when agents are connected <i>directly</i> without a model hierarchy.
 *
 */
public final class DirectMessageForwarding implements MessageForwardingStrategy {

	@Override
	public void forwardMessages(Collection<Agent> agentList) {
		// part I: get list of all ports carrying an outgoing message
		final Collection<Port> destinations=new ArrayList<>();
		final Collection<Port> sources=listPortsWithOutgoingMsg(agentList);
		// part II: do forwarding of messages, only one Step because of the direct
		// connections
		for (final Port src : sources) destinations.addAll(src.forwardMessages());
		// Only one copy cycle because there should be only direct connections
	}

}
