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
import org.simplesim.model.AbstractDomain;

/**
 * This implementation of a {@link IMessageForwardingStrategy} starts at the
 * outports and works its way recursively through the connected ports.
 *
 * Choose this strategy if you have a model with short message pipelines and
 * messaging mostly between {@link AbstractAgent} of the same
 * {@link AbstractDomain}.
 *
 * @author Rene Kuhlemann
 *
 */
public final class RecursiveMessageForwarding implements IMessageForwardingStrategy {

	private static final int MAX_RECURSION_LEVEL=50;

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
		final Collection<AbstractPort> ports=listPortsWithOutgoingMsg(agentList);
		// part II: do recursive forwarding of messages
		nextLevel(ports,0);
	}

	private void nextLevel(Collection<AbstractPort> sources, int level) {
		if (level>=MAX_RECURSION_LEVEL) throw new StackOverflowError(
				"Recursion level during message forwarding exceeded max. depth of "+MAX_RECURSION_LEVEL);
		final Collection<AbstractPort> destinations=new ArrayList<>();
		for (final AbstractPort src : sources) {
			// make sure there is another connection
			if (src.isEndPoint()) continue;
			// copy messages and add new destinations to list
			destinations.addAll(src.copyMessages());
		}
		// make destinations to sources in the next level. If list is empty, all
		// messages had arrived
		if (!destinations.isEmpty()) nextLevel(destinations,level+1);
	}

}
