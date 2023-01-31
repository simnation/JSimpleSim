/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.messaging;

import java.util.Collection;

import org.simplesim.model.ModelEntity;

/**
 * Ports are used to send messages within the simulation model.
 * <p>
 * In the phase of model building, ports are connected with each others via the
 * {@code connectTo} method. The sending entity puts a message on its outport.
 * The message is then routed along the established connections. A port that has
 * no further connection is always an end-point (destination of delivery). Thus,
 * connections are always <i>directed</i> from outport (source) to inport
 * (destination).
 * <p>
 * Each port must also implement its own {@code copyMessages} strategy to
 * support message forwarding during the simulation run.
 *
 * @see SinglePort
 * @see MultiPort
 * @see SwitchPort
 * @see org.simplesim.model.RoutingDomain.RoutingPort RoutingPort
 */
public interface Port {

	/**
	 * Connects this port to another one.
	 * <p>
	 * Note that connections are directed.
	 *
	 * @param target the other part of the connection
	 */
	void connect(Port target);

	/**
	 * Disconnects this port from another one.
	 *
	 * @param target the port to be removed
	 * @exception ModelEntity.PortConnectionException if the port is not connected
	 *                                                with the target or the
	 *                                                simulation is running
	 */
	void disconnect(Port target);

	/**
	 * Tests if the port is the end point of a connection.
	 *
	 * @return true if it is end point (destination port)
	 */
	boolean isEndPoint();

	/**
	 * Tests if the port is connected to another one.
	 *
	 * @param port the port to test
	 * @return true if this port is connected to the other port
	 */
	boolean isConnectedTo(Port port);

	/**
	 * Implements a message forwarding strategy specific for the port class.
	 * <p>
	 * Port must be connected to other ports, please check if it is an end point
	 * beforehand!
	 * <p>
	 * Note: Only to be used by an implementation of
	 * {@link MessageForwardingStrategy}
	 *
	 * @return list of destination ports that need further forwarding
	 */
	Collection<Port> forwardMessages();

	/**
	 * Clears message queue of this port.
	 */
	void clearMessages();

	/**
	 * Counts messages in queue of this port.
	 *
	 * @return number of messages, can be zero
	 */
	int countMessages();

	/**
	 * Checks if the port has messages.
	 *
	 * @return true if there are messages
	 */
	boolean hasMessages();

	/**
	 * Returns <u>and</u> removes the last element in the message queue.
	 *
	 * @return last element in message list or null if list is empty
	 */
	<M extends AbstractMessage<?>> M poll();

	/**
	 * Returns the message queue.
	 * <p>
	 * Elements must be removed manually by calling the {@link #clearMessages()}
	 * method
	 *
	 * @return the message queue
	 */
	<M extends AbstractMessage<?>> Collection<M> readAll();

	/**
	 * Puts a message to be forwarded on this port.
	 *
	 * @param msg the message
	 */
	void write(AbstractMessage<?> msg);

	/**
	 * Puts several messages to be forwarded on this port.
	 *
	 * @param msg collection of messages
	 */
	void writeAll(Collection<AbstractMessage<?>> msg);

	ModelEntity getParent();

}
