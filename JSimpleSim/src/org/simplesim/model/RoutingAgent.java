/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import java.util.Collections;

import org.simplesim.core.routing.SinglePort;
import org.simplesim.core.scheduling.IEventQueue;

/**
 * 
 *
 */
public abstract class RoutingAgent<S extends IAgentState, E>  extends AbstractAgent<S, E> {

	/**
	 * @param queue
	 * @param s
	 */
	public RoutingAgent(IEventQueue<E> queue, S s) {
		super(queue,s);
		init();
	}
	
	/**
	 * @param queue
	 * @param s
	 */
	public RoutingAgent(S s) {
		super(s);
		init();
	}
	
	private void init() {
		setInportList(Collections.singletonList(new SinglePort(this)));
		setOutportList(Collections.singletonList(new SinglePort(this)));
	}

	
}
