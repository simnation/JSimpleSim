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
import java.util.List;

import org.simplesim.model.BasicModelEntity;

/**
 * Port for routing messages automatically.
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
		destinations.add(port);
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
		final List<AbstractPort> result=new ArrayList<>();
		while (hasMessages()) {
			final Message<?> msg=read(); // message is also removed in this step!
			if (!(msg instanceof RoutedMessage))
				throw new UnsupportedOperationException(getClass().getSimpleName()+" can only handle RoutedMessages!");
			final int nxtlvl=getParent().getLevel(); // get index of next hierarchy level, root not indexed
			final int index=((RoutedMessage) msg).getDestIndex(nxtlvl);
			final AbstractPort dest=destinations.get(index); // find the right port for forwarding
			dest.write(msg);
			if (!result.contains(dest)) result.add(dest); // allow no duplicates in destination list
		}
		return result;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
