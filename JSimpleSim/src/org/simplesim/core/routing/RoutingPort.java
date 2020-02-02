/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.simplesim.model.BasicModelEntity;

/**
 * Port for automatic message routing.
 * <p>
 * Routing is done by reading the messages' destination descriptions and sending
 * the message along the right connection accordingly. Thus only a
 * {@link RoutedMessage} can be handled by this port since it contains
 * additional address information.
 * <p>
 * The operation modus is similar to a {@link MulitPort}, but the messages is
 * only forward to ONE port of the destination list, <i>not</i> all.
 *
 */
public final class RoutingPort extends AbstractPort {

	private final ArrayList<AbstractPort> destinations=new ArrayList<>();

	public RoutingPort(BasicModelEntity model) {
		super(model);
	}


	@Override
	public void connectTo(AbstractPort port) {
		final int level=getParent().getLevel(); // get hierarchy level of current domain
		final int index=port.getParent().getAddress()[level]; // 
		destinations.ensureCapacity(index+1);
		destinations.set(index,port);
	}

	@Override
	public void disconnect(AbstractPort port) {
		final int index=destinations.indexOf(port);
		if (index==-1) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
		destinations.set(index,null); // delete the element without changing indices!
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Collection<AbstractPort> copyMessages() {
		final Set<AbstractPort> result=new HashSet<>(); // set to ensure no duplicates in destination list
		while (hasMessages()) {
			final int nxtlvl=getParent().getLevel(); // get index of next hierarchy level, root not indexed
			final RoutedMessage msg=(RoutedMessage) read(); // message is also removed in this step!
			final int index=msg.getDestIndex(nxtlvl);
			final AbstractPort dest=destinations.get(index); // find the right port for forwarding
			dest.write(msg);
			result.add(dest); 
		}
		return result;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
