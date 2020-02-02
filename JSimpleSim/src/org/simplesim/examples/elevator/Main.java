/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.routing.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.AbstractSimulator;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.ConcurrentTSSimulator;
import org.simplesim.simulator.SequentialDESimulator;

/**
 * Example of a multi-domain agent system with direct messaging
 * <p>
 * This 
 *
 */
public class Main {
	
	private static final int NUMBER_OF_VISITORS=800;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Model model=new Model(NUMBER_OF_VISITORS);
		final View view=new View(model);
		final AbstractSimulator simulator=new ConcurrentDESimulator(model,new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(24*Time.HOUR));
		view.close();
	}

}
