/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.core.messaging;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.simplesim.model.ModelEntity;

/**
 * Port to connect a port to exactly one other port.
 * <p>
 * Inports are always a {@code SingePort} by default.
 *
 * @see Message
 */
public final class SinglePort extends AbstractPort {

	/**
	 * Save single destination port in a list with fixed size of one. This facilitates message forwarding.
	 */
	private List<Port> destination=Collections.emptyList();

	public SinglePort(ModelEntity model) {
		super(model);
	}

	@Override
	public void connect(Port port) {
		if (!isEndPoint())
			throw new ModelEntity.PortConnectionException("Connection of SinglePort alread in use in "+getParent().getFullName());
		destination=Collections.singletonList(port);
	}

	@Override
	public void disconnect(Port port) {
		if (!isConnectedTo(port)) throw new ModelEntity.PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
		destination=Collections.emptyList();
	}

	@Override
	public boolean isEndPoint() {
		return destination.isEmpty();
	}

	@Override
	public boolean isConnectedTo(Port port) {
		return port.equals(getConnection());
	}

	@Override
	public Collection<Port> forwardMessages() {
		if (!hasMessages()) return Collections.emptyList();
		getConnection().writeAll(this.readAll());
		clearMessages();
		return destination;
	}

	/**
	 * Returns the destination of this port.
	 *
	 * @return destination port of this connection
	 */
	public Port getConnection() {
		return destination.get(0);
	}

}
