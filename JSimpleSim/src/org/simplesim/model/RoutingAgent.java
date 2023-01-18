/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.EventQueue;

/**
 * Extension of an {@code AbstractAgent} to enable massage routing.
 * <p>
 * When using message routing, there is no direct connection between source and destination. Rather the address is
 * specified in the message envelope. This can be used to route the message to the right destination. So the agent needs
 * only one inport and one outport. These are connected to the parent domain which does the actual routing. Therefore,
 * the parent must be a {@code RoutingDomain}.
 * <p>
 * Note: Connection of ports is done automatically when adding this agent to a {@code RoutingDomain}.
 *
 * @see BasicAgent
 * @see RoutingDomain
 * @see org.simplesim.core.messaging.RoutedMessage RoutedMessage
 */
public abstract class RoutingAgent<S extends State, E> extends BasicAgent<S, E> {

	/**
	 * {@inheritDoc}
	 */
	public RoutingAgent(EventQueue<E> queue, S s) {
		super(queue,s);
		setOutport(new SinglePort(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public RoutingAgent(S s) {
		super(s);
		setOutport(new SinglePort(this));
	}

}
