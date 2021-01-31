/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.messaging;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

import org.simplesim.model.BasicModelEntity;
import org.simplesim.model.BasicModelEntity.UniqueConstraintViolationException;

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

	private final Map<BasicModelEntity, AbstractPort> destinations=new IdentityHashMap<>();

	public SwitchPort(BasicModelEntity model) {
		super(model);
	}

	@Override
	public void connectTo(AbstractPort port) {
		if (isConnectedTo(port)) throw new UniqueConstraintViolationException("SwitchPort in "
				+this.getParent().getFullName()+" may not be connected twice to "+port.getParent().getFullName());
		destinations.put(port.getParent(),port);
	}

	@Override
	public void disconnect(AbstractPort port) {
		if (!destinations.remove(port.getParent(),port)) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Collection<AbstractPort> forwardMessages() {
		if (!hasMessages()) return Collections.emptyList();
		Collection<AbstractPort> result=new HashSet<>();
		for (AbstractMessage<?> msg : readAll()) {
			AbstractPort port=destinations.get(msg.getDestination());
			if (port==null) throw new PortConnectionException(
					"No destination port found for "+msg.toString()+" in "+getParent().getFullName());
			port.write(msg);
			result.add(port);
		}
		clearMessages();
		return result;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.containsValue(port);
	}

}
