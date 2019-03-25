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
public final class SinglePort extends AbstractPort {

	private AbstractPort destination=null;

	public SinglePort(BasicModelEntity model) {
		super(model);
	}

	@Override
	public void connectTo(AbstractPort port) {
		if (destination!=null)
			throw new PortConnectionException("Connection of SinglePort alread in use in "+getParent().getFullName());
		destination=port;
	}

	@Override
	public void disconnect(AbstractPort port) {
		if (destination!=port) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
		destination=null;
	}

	@Override
	public boolean isEndPoint() {
		return destination==null;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return port.equals(destination);
	}

	@Override
	public Set<AbstractPort> copyMessages() {
		destination.writeAll(this.readAll());
		this.clear();
		final Set<AbstractPort> result=new HashSet<>(1);
		result.add(destination);
		return result;
	}

	public AbstractPort getConnection() {
		return destination;
	}

}
