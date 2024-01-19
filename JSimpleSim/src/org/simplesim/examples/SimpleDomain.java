/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples;

import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.messaging.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.RoutingDomain;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * Simple implementation of a {@code RoutingDomain}
 */
public class SimpleDomain extends RoutingDomain {

	public SimpleDomain() {
	}

	public static void main(String[] args) {
		SimpleDomain root=new SimpleDomain();
		root.setAsRootDomain();
		new SimpleAgent().addToDomain(root);
		SimpleDomain subdomain=new SimpleDomain();
		new SimpleAgent().addToDomain(subdomain);
		new SimpleAgent().addToDomain(subdomain);
		subdomain.addToDomain(root);
		final MessageForwardingStrategy fs=new RecursiveMessageForwarding();
		Simulator simulator=new SequentialDESimulator(root,new HeapEventQueue<>(),fs);
		simulator.runSimulation(Time.INFINITY);
	}

}