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

import org.simplesim.model.AbstractAgent;

/**
 * Interface to implement various strategies to forward messages during
 * simulation
 *
 * @author Rene Kuhlemann
 *
 */
public interface IMessageForwardingStrategy {

	void forwardMessages(List<AbstractAgent<?, ?>> sender);

	default Set<AbstractPort> listPortsWithOutgoingMsg(List<AbstractAgent<?, ?>> agentList) {
		final Set<AbstractPort> result=new HashSet<>();
		for (final AbstractAgent<?, ?> agent : agentList)
			for (final AbstractPort port : agent.getOutports()) if (port.hasValue()) result.add(port);
		return result;
	}

}
