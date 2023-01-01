/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples;

import org.simplesim.core.messaging.ForwardingStrategy;
import org.simplesim.core.messaging.RoutedMessageForwarding;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.RoutingDomain;
import org.simplesim.simulator.DynamicDecorator;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;


/**
 * Simple implementation of a {@code RoutingDomain} 
 */
public class SimpleDomain extends RoutingDomain {

	public SimpleDomain() {
		super();
	}
	
	public static void main(String[] args) {
		SimpleDomain root=new SimpleDomain();
		root.setAsRootDomain();
		root.addEntity(new SimpleAgent());
		SimpleDomain subdomain=new SimpleDomain();
		subdomain.addEntity(new SimpleAgent());
		subdomain.addEntity(new SimpleAgent());
		root.addEntity(subdomain);
		ForwardingStrategy fs=new RoutedMessageForwarding(root);
		Simulator simulator=new DynamicDecorator(new SequentialDESimulator(root,new HeapEventQueue<>(),fs));
		simulator.runSimulation(new Time(Time.INFINITY));
	}

}