/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import java.util.HashSet;
import java.util.Set;

import org.simplesim.model.BasicModelEntity;

/**
 * @author Rene Kuhlemann
 *
 */
public final class MultiPort extends AbstractPort {

	private final Set<AbstractPort> destinations=new HashSet<>();

	public MultiPort(BasicModelEntity model) {
		super(model);
	}

	@Override
	public void connectTo(AbstractPort port) {
		destinations.add(port);
	}

	@Override
	public void disconnect(AbstractPort port) {
		if (!destinations.remove(port)) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Set<AbstractPort> copyMessages() {
		for (final AbstractPort dest : destinations) dest.writeAll(this.readAll());
		this.clear();
		return destinations;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
