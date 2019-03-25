/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simplesim.core.exceptions.PortTypeMismatchException;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;

/**
 * This implementation of a {@link IMessageForwardingStrategy} works with an
 * intermeshed network.
 *
 * Choose this strategy if you have a model where each agent is connected
 * directly to its message partners an there is no intersecting
 * {@link AbstractDomain}.
 *
 * @author Rene Kuhlemann
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
	public void forwardMessages(List<AbstractAgent<?, ?>> agentList) {
		// part I: get list of all ports carrying an outgoing message
		final Set<AbstractPort> destinations=new HashSet<>();
		final Set<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		// part II: do forwarding of messages, only one Step because of the direct
		// connections
		for (final AbstractPort src : sources) destinations.addAll(src.copyMessages());
		for (final AbstractPort dest : destinations) // make sure there is no other connection
			if (!dest.isEndPoint()) throw new PortTypeMismatchException(
					"Next port should be end-point when using the direct message forwarding strategy!");
	}

}
