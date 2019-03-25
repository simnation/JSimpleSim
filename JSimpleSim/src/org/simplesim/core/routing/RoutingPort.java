/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.simplesim.core.exceptions.OperationNotAllowedException;
import org.simplesim.model.BasicModelEntity;

/**
 * This port class is build for automatically routing messages. This is done by
 * reading the messages' destination descriptions and routing the message
 * accordingly along the right connection. Thus only {@link RoutedMessage} can
 * be handled by this port which contain additional address information. The
 * operation modus is similar to a {@link MulitPort}, but the messages is only
 * forward to ONE destination port in list, NOT all.
 *
 * @author Rene Kuhlemann
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
	public Set<AbstractPort> copyMessages() {
		final Set<AbstractPort> result=new HashSet<>();
		while (hasValue()) {
			final Message<?> msg=read(); // message is also removed in this step!
			if (!(msg instanceof RoutedMessage))
				throw new OperationNotAllowedException(getClass().getSimpleName()+" can only handle RoutedMessages!");
			final int nxtlvl=getParent().getLevel(); // get index of next hierarchy level, root not indexed
			final int index=((RoutedMessage) msg).getDestIndex(nxtlvl);
			final AbstractPort dest=destinations.get(index); // find the right port for forwarding
			dest.write(msg);
			result.add(dest); // no duplicates in destination list because of Set
		}
		return result;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
