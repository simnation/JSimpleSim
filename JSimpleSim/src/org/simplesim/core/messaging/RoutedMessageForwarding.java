/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import static org.simplesim.model.BasicModelEntity.ROOT_LEVEL;
import org.simplesim.model.RoutingDomain;

/**
 * Implementation of {@code ForwardingStrategy} for the routing concept.
 * <p>
 * This implementation first assigns outports with messages to layers according to the level of their parent {@link AbstractDomain}.
 * Then forwarding starts at the bottom most port and works its way up to the root. In a second step, messages are
 * copied in the root node of the model tree. In a third step, messages are forwarded top-down in a similar way.
 * <p>
 * Note: This strategy only works with models using the routing concept.
 * 
 * @see RoutingDomain
 * @see DefaultMessageForwarding
 * 
 */
public final class RoutedMessageForwarding implements ForwardingStrategy {

	private final RoutingDomain root;
	private final List<Set<AbstractPort>> layers=new ArrayList<>();
	
	
	public RoutedMessageForwarding(RoutingDomain r) {
		root=r;
	}

	/*
	 * (non-Javadoc)
	 * @see org.devs.core.ports.IMessageForwardingStrategy#forwardMessages(java.util. List)
	 */
	@Override
	public void forwardMessages(Collection<AbstractAgent<?, ?>> agentList) {
		// part I: build a list of sets, each set representing a level of the overall
		// model and containing all respective ports with outgoing messages
		final Collection<AbstractPort> sources=listPortsWithOutgoingMsg(agentList);
		if (sources.isEmpty()) return;
		for (final AbstractPort port : sources) {
			final int level=port.getParent().getLevel();
			while (layers.size()<=level) layers.add(new HashSet<AbstractPort>());
			layers.get(level).add(port);
		}
		// part II: copy all message from bottom most level upwards to the root layer
		doHierarchicalCopyingUp();
		// part III: copy messages from outport to inport of root layer
		root.getOutport().forwardMessages();
		layers.get(ROOT_LEVEL).clear();
		layers.get(ROOT_LEVEL).add(root.getInport());
		// part IV: copy messages from root layer down to their destination
		doHierarchicalCopyingDown();
		// part V: recycling - empty sets for next usage to save memory and time
		for (final Set<AbstractPort> set : layers) set.clear();
	}

	/**
	 * Copy messages from bottom most model to the top of the model tree.
	 */
	private void doHierarchicalCopyingUp() {
		for (int level=layers.size()-1; level>ROOT_LEVEL; level--) {
			final Set<AbstractPort> sources=layers.get(level);
			final Set<AbstractPort> destinations=layers.get(level-1);
			for (final AbstractPort src : sources) {
				// make sure there is another connection
				if (src.isEndPoint()) continue;
				// copy messages and add new destinations to list
				destinations.addAll(src.forwardMessages());
			}
			sources.clear(); // all ports processed in this level, important for re-use next time!
		}
	}

	/**
	 * Copy messages from top of the model tree to the bottom most level.
	 */
	private void doHierarchicalCopyingDown() {
		for (int level=ROOT_LEVEL+1; level<layers.size(); level++) {
			final Set<AbstractPort> sources=layers.get(level-1);
			final Set<AbstractPort> destinations=layers.get(level);
			for (final AbstractPort src : sources) {
				// make sure there is another connection
				if (src.isEndPoint()) continue;
				// copy messages and add new destinations to list
				destinations.addAll(src.forwardMessages());
			}
			sources.clear(); // all ports processed in this level, important for re-use next time!
			if (destinations.isEmpty()) return; // no more ports to process
		}
	}

}
