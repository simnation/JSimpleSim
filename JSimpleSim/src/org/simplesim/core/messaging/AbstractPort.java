/**
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
import java.util.List;

import org.simplesim.model.BasicModelEntity;

/**
 * Ports are used to send messages within a model.
 * <p>
 * In the phase of model building, ports are connected with each others via the
 * {@code connectTo} method. The sending entity puts a {@code Message} on its
 * outport. The message is then routed along the established connections. A port
 * that has no further connection is always an end-point (destination of
 * delivery). Thus, connections are always <i>directed</i> from source outport
 * to destination inport.
 * <p>
 * Each port must also implement its own {@code copyMessages} strategy to
 * support message forwarding during the simulation run.
 *
 * @see SinglePort
 * @see MultiPort
 * @see SwitchPort
 * @see org.simplesim.model.RoutingDomain.RoutingPort RoutingPort
 */
public abstract class AbstractPort {

	/** parent model that contains this port */
	private final BasicModelEntity parent;

	/**
	 * List of messages that are communicated through this port. The initial size is
	 * set to 1. Thus memory consumption is low in the beginning, it will
	 * automatically be adapted if needed later on.
	 */
	private final List<AbstractMessage<?>> messages = new ArrayList<>(1);

	/**
	 * Exception to be thrown if there is an error in port handling
	 */
	@SuppressWarnings("serial")
	protected static class PortConnectionException extends RuntimeException {
		public PortConnectionException(String message) {
			super(message);
		}
	}

	public AbstractPort(BasicModelEntity model) {
		parent = model;
	}

	/**
	 * Connects this port to another one.
	 *
	 * @param target the other part of the connection
	 */
	public abstract void connectTo(AbstractPort target);

	/**
	 * Disconnects this port from another on.
	 *
	 * @param target the port to be removed
	 * @exception PortConnectionException if the port is not connected with the
	 *                                    target or the simulation is running
	 */
	public abstract void disconnect(AbstractPort target);

	/**
	 * Tests if the port is the end point of a connection.
	 *
	 * @return true if it is end point (destination port)
	 */
	public abstract boolean isEndPoint();

	/**
	 * Tests if the port is connected to another one.
	 *
	 * @param port the port to test
	 * @return true if this port is connected to the other port
	 */
	public abstract boolean isConnectedTo(AbstractPort port);

	/**
	 * Implements a message forwarding strategy specific for the port class.
	 * <p>
	 * Port must be connected to other ports, please check if it is an end point
	 * beforehand!
	 * <p>
	 * Note: Only to be used by an implementation of {@link ForwardingStrategy}
	 *
	 * @return list of destination ports that need further forwarding
	 */
	protected abstract Collection<AbstractPort> forwardMessages();

	/**
	 * Clears message queue of this port.
	 */
	public final void clearMessages() {
		messages.clear();
	}

	/**
	 * Counts messages in queue of this port.
	 *
	 * @return number of messages, can be zero
	 */
	public final int countMessages() {
		return messages.size();
	}

	/**
	 * Checks if the port has messages.
	 *
	 * @return true if there are messages
	 */
	public final boolean hasMessages() {
		return !messages.isEmpty();
	}

	/**
	 * Returns <u>and</u> removes the last element in the message queue.
	 *
	 * @return last element in message list or null if list is empty
	 */
	@SuppressWarnings("unchecked")
	public final <M extends AbstractMessage<?>> M poll() {
		if (!hasMessages())
			return null;
		return (M) messages.remove(countMessages() - 1);
	}

	/**
	 * Returns the message queue.
	 * <p>
	 * Elements must be removed manually by calling the {@link #clearMessages()}
	 * method
	 *
	 * @return the message queue
	 */
	public final Collection<AbstractMessage<?>> readAll() {
		return messages;
	}

	/**
	 * Puts a message to be forwarded on this port.
	 *
	 * @param message the message
	 */
	public final void write(AbstractMessage<?> message) {
		messages.add(message);
	}

	/**
	 * Puts several messages to be forwarded on this port.
	 *
	 * @param m collection of messages
	 */
	public final void writeAll(Collection<AbstractMessage<?>> m) {
		messages.addAll(m);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		return (this == obj);
	}

	public final BasicModelEntity getParent() {
		return parent;
	}

}
