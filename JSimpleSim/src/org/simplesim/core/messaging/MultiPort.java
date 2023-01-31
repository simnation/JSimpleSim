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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.simplesim.model.ModelEntity;

import static org.simplesim.model.BasicModelEntity.UniqueConstraintViolationException;


/**
 * Port to send the same message to several inports.
 * <p>
 * Messages of the outport are copied to all connected inports. Does not need the destination information
 * of a message.
 * <p>
 * Only use as outport. Can be used with {@code DefaultMessageForwarding} or {@code DirectMessageForwarding}.
 *
 * @see Message
 * @see SinglePort
 */
public final class MultiPort extends AbstractPort {

	private final List<Port> destinations=new ArrayList<>();

	public MultiPort(ModelEntity model) {
		super(model);
	}

	@Override
	public void connect(Port port) {
		if (destinations.contains(port)) throw new UniqueConstraintViolationException("MultiPort in "+this.getParent().getFullName()
				+" may not be connected twice to "+port.getParent().getFullName());
		destinations.add(port);
	}

	@Override
	public void disconnect(Port port) {
		if (!destinations.remove(port)) throw new ModelEntity.PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Collection<Port> forwardMessages() {
		if (!hasMessages()) return Collections.emptyList();
		for (final Port dest : destinations) dest.writeAll(this.readAll());
		clearMessages();
		return destinations;
	}

	@Override
	public boolean isConnectedTo(Port port) {
		return destinations.contains(port);
	}

}
