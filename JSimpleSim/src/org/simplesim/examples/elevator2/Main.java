/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator2;

import org.simplesim.core.routing.ForwardingStrategy;
import org.simplesim.core.routing.RoutedMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.AbstractSimulator;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.DynamicDecorator;
import org.simplesim.simulator.Simulator;
import org.simplesim.simulator.SequentialDESimulator;

/**
 * Example of a multi-domain agent system with direct messaging
 * <p>
 * This
 */
public class Main {

	private static final int NUMBER_OF_VISITORS=600;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Building model=Building.getInstance();
		final Floor lobby=new Floor(Building.LOBBY);
		model.addEntity(lobby);
		for (int i=0; i<NUMBER_OF_VISITORS; i++) lobby.addEntity(new Visitor());
		for (int floor=1; floor<=Building.MAX_FLOOR; floor++) {
			model.addEntity(new Floor(floor));
		}
		final View view=new View(model);
		ForwardingStrategy fs=new RoutedMessageForwarding(model);
		//final Simulator simulator=new DynamicDecorator(new ConcurrentDESimulator(model,fs));
		final Simulator simulator=new DynamicDecorator(new SequentialDESimulator(model,fs));
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(12*Time.HOUR));
		view.close();
	}

}
