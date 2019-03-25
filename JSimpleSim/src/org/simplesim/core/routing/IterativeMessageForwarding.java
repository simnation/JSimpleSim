/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;

/**
 * This implementation of a {@link IMessageForwardingStrategy} first assigns
 * outports to a layer according to the level of the next
 * {@link AbstractDomain}. Then forwarding starts at the bottom most port and
 * works its way up to the root. In a second step, messages are forwarded
 * top-down in a similar way.
 *
 * Choose this strategy if you have a model with long message pipelines across
 * several domains. In each level, all relevant port will be accumulation and
 * messages are forwarded in one step to the next level.
 *
 * @author Rene Kuhlemann
 *
 */
public final class IterativeMessageForwarding implements IMessageForwardingStrategy {

	private final List<Set<AbstractPort>> layers=new ArrayList<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.devs.core.ports.IMessageForwardingStrategy#forwardMessages(java.util.
	 * List)
	 */
	@Override
	public void forwardMessages(List<AbstractAgent<?, ?>> agentList) {
		// part I: build a list of sets, each set representing a level of the overall
		// model and containing all respective ports with outgoing messages
		final Set<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		for (final AbstractPort port : sources) {
			final int level=port.getParent().getParent().getLevel();
			if (level<0) continue;
			while (layers.size()<=level) layers.add(new HashSet<AbstractPort>());
			layers.get(level).add(port);
		}
		// part II: copy all message from bottom most level upwards and then from
		// topmost level down again
		doHierarchicalCopyingUp();
		doHierarchicalCopyingDown();
		// part III: recycling - empty sets for next usage to save memory and time
		for (final Set<AbstractPort> set : layers) set.clear();
	}

	/**
	 * Copy messages from bottom most model to the top of the model tree.
	 *
	 */
	private void doHierarchicalCopyingUp() {
		for (int level=layers.size()-1; level>=1; level--) {
			final Set<AbstractPort> sources=layers.get(level);
			final Set<AbstractPort> destinations=layers.get(level-1);
			for (final AbstractPort src : sources) {
				// make sure there is another connection
				if (src.isEndPoint()) continue;
				// copy messages and add new destinations to list
				destinations.addAll(src.copyMessages());
			}
			sources.clear(); // all ports processed in this level, important for re-use next time!
		}
	}

	/**
	 * Copy messages from top of the model tree to the bottom most level.
	 *
	 */
	private void doHierarchicalCopyingDown() {
		for (int level=0; level<layers.size(); level++) {
			final Set<AbstractPort> sources=layers.get(level);
			final Set<AbstractPort> destinations=layers.get(level+1);
			for (final AbstractPort src : sources) {
				// make sure there is another connection
				if (src.isEndPoint()) continue;
				// copy messages and add new destinations to list
				destinations.addAll(src.copyMessages());
			}
			sources.clear(); // all ports processed in this level, important for re-use next time!
			if (destinations.isEmpty()) return; // no more ports to process
		}
	}

}
