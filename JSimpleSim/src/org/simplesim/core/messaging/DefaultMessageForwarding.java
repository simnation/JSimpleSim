/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.core.messaging;

import java.util.ArrayList;
import java.util.Collection;
import org.simplesim.model.AbstractAgent;

/**
 * Recursive version of a {@code ForwardingStrategy}.
 * <p>
 * This implementation starts at all outports with messages and works its way through 
 * the connected ports. With this strategy some ports may be called several times.
 * <p>
 * This implementation generally should work with all types of ports but might be less efficient than more specialized strategies.
 * 
 */
public final class DefaultMessageForwarding implements ForwardingStrategy {

	private static final int MAX_RECURSION_LEVEL=100;

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
		Collection<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		Collection<AbstractPort> destinations=new ArrayList<>();
		// part II: do recursive forwarding of messages
		for (int level=0; level<MAX_RECURSION_LEVEL; level++) {
			for (final AbstractPort src : sources) {
				// make sure there is another connection
				if (src.isEndPoint()) continue;
				// copy messages and add new destinations to list
				destinations.addAll(src.forwardMessages());
			}
			if (destinations.isEmpty()) return; // no more messages to forward
			final Collection<AbstractPort> temp=sources;
			sources=destinations;
			destinations=temp;
			destinations.clear();
		}
		throw new ForwardingFailureException(
			"Recursion level during message forwarding exceeded max. depth of "+MAX_RECURSION_LEVEL);
	}
	
	/*public void forwardMessages(Collection<AbstractAgent<?, ?>> agentList) {
		// part I: get list of all ports carrying an outgoing message
		final Collection<AbstractPort> ports=listPortsWithOutgoingMsg(agentList);
		// part II: do recursive forwarding of messages
		nextLevel(ports,0);
	}
	
	

	private void nextLevel(Collection<AbstractPort> sources, int level) {
		if (level>=MAX_RECURSION_LEVEL) throw new ForwardingFailureException(
				"Recursion level during message forwarding exceeded max. depth of "+MAX_RECURSION_LEVEL);
		final Collection<AbstractPort> destinations=new ArrayList<>();
		for (final AbstractPort src : sources) {
			// make sure there is another connection
			if (src.isEndPoint()) continue;
			// copy messages and add new destinations to list
			destinations.addAll(src.forwardMessages());
		}
		// make destinations to sources in the next level. If list is empty, all
		// messages had arrived
		if (!destinations.isEmpty()) nextLevel(destinations,level+1);
	}*/

}
