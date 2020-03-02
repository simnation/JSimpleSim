/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.routing.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.AbstractSimulator;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.SequentialDESimulator;

/**
 * Example of a multi-domain agent system with direct messaging
 * <p>
 * This 
 *
 */
public class Main {
	
	private static final int NUMBER_OF_VISITORS=600;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Model model=new Model(NUMBER_OF_VISITORS);
		final View view=new View(model);
		//final AbstractSimulator simulator=new ConcurrentDESimulator(model,new DirectMessageForwarding());
		final AbstractSimulator simulator=new SequentialDESimulator(model,new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(12*Time.HOUR));
		view.close();
	}

}
