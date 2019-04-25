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
import java.util.Collections;
import java.util.List;

import org.simplesim.core.exceptions.NotUniqueException;
import org.simplesim.model.BasicModelEntity;

/**
 * @author Rene Kuhlemann
 *
 */
public final class MultiPort extends AbstractPort {

	private final List<AbstractPort> destinations=new ArrayList<>();

	public MultiPort(BasicModelEntity model) {
		super(model);
	}

	@Override
	public void connectTo(AbstractPort port) {
		if (destinations.contains(port)) throw new NotUniqueException("MultiPort in "+this.getParent().getFullName()
				+" has already be connected to "+port.getParent().getFullName());
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
	public Collection<AbstractPort> copyMessages() {
		if (!hasValue()) return Collections.emptyList();
		for (final AbstractPort dest : destinations) dest.writeAll(this.readAll());
		this.clear();
		return destinations;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
