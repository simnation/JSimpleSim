/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.View;
import org.simplesim.examples.elevator.stat.StaticElevator;
import org.simplesim.examples.elevator.stat.StaticModel;
import org.simplesim.examples.elevator.stat.StaticVisitor;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * 
 *
 */
public class StaticMain {
	
	public static void main(String[] args) {
		final StaticModel model=new StaticModel();

		// build simulation model
		final StaticElevator elevator=model.getElevator();
		model.addEntity(elevator);
		for (int index=1; index<=Limits.VISITORS; index++) {
			final StaticVisitor visitor=new StaticVisitor();
			model.addEntity(visitor);
			elevator.addOutport(visitor).connectTo(visitor.getInport());
			visitor.getOutport().connectTo(elevator.getInport());
		}
		
		final View view=new View(elevator.getState());
		//final AbstractSimulator simulator=new ConcurrentDESimulator(model,new DirectMessageForwarding());
		final Simulator simulator=new SequentialDESimulator(model,new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

}
