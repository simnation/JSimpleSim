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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

import org.simplesim.model.ModelEntity;
import org.simplesim.model.ModelEntity.UniqueConstraintViolationException;

/**
 * Port to send a message to one of several inports.
 * <p>
 * Messages are sent from this port implementation to a connected inport. The inport is indexed by the 
 * message destination, so there can be only one {@code SwitchPort} along the way of message.
 * <p>
 * Note: Only use as outport.
 *
 * @see Message
 * @see SinglePort
 * @see MultiPort
 * @see RecursiveMessageForwarding
 */
public final class SwitchPort extends AbstractPort {

	private final Map<ModelEntity, Port> destinations=new IdentityHashMap<>();

	public SwitchPort(ModelEntity model) {
		super(model);
	}

	@Override
	public void connect(Port port) {
		if (isConnectedTo(port)) throw new UniqueConstraintViolationException("SwitchPort in "
				+this.getParent().getFullName()+" may not be connected twice to "+port.getParent().getFullName());
		destinations.put(port.getParent(),port);
	}

	@Override
	public void disconnect(Port port) {
		if (!destinations.remove(port.getParent(),port)) throw new ModelEntity.PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Collection<Port> forwardMessages() {
		if (!hasMessages()) return Collections.emptyList();
		Collection<Port> result=new HashSet<>();
		for (AbstractMessage<?> msg : readAll()) {
			Port port=destinations.get(msg.getDestination());
			if (port==null) throw new ModelEntity.PortConnectionException(
					"No destination port found for "+msg.toString()+" in "+getParent().getFullName());
			port.write(msg);
			result.add(port);
		}
		clearMessages();
		return result;
	}

	@Override
	public boolean isConnectedTo(Port port) {
		return destinations.containsValue(port);
	}

}
