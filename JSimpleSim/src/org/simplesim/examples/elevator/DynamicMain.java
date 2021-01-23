/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.ForwardingStrategy;
import org.simplesim.core.messaging.RoutedMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.dyn.DynamicElevator;
import org.simplesim.examples.elevator.dyn.DynamicModel;
import org.simplesim.examples.elevator.dyn.DynamicVisitor;
import org.simplesim.examples.elevator.dyn.Floor;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.View;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.DynamicDecorator;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * Example of a multi-domain agent system with routed messaging and dynamic model changes
 * <p>
 * To illustrate differences of a static and a dynamic modeling approach, both are used with the same simulation problem: the steering strategy of an elevator.
 * Common data structure, the steering algorithm and the graphical representation are shared, so the focus lies on
 * the differences of both approaches:
 * <p>
 * <u>Static model:</u><ul>
 * <li> Visitors store their current floor in their state.
 * <li> Ports of elevator and visitor are connected directly.
 * <li>  
 *  </ul>
 * 
 *  
 * (no model changes) (model is changed during the 
 * simulation run)
 *  nThis is the dynamic variant of the elevator simulation example to illustrate differences from the static apporach. 
 */
public class DynamicMain {

	/**
	 * Example main method on how to set up a dynamic simulation model
	 */
	public static void main(String[] args) {
		final DynamicModel model=new DynamicModel();

		// build simulation model
		final DynamicElevator elevator=model.getElevator();
		model.addEntity(elevator);
		final Floor lobby=new Floor(Limits.LOBBY);
		model.addEntity(lobby);
		for (int i=0; i<Limits.VISITORS; i++) lobby.addEntity(new DynamicVisitor(model));
		for (int floor=1; floor<=Limits.MAX_FLOOR; floor++) model.addEntity(new Floor(floor));

		final View view=new View(elevator.getState());
		final ForwardingStrategy fs=new RoutedMessageForwarding(model);
		final Simulator simulator=new DynamicDecorator(new ConcurrentDESimulator(model,fs));
		//final Simulator simulator=new DynamicDecorator(new SequentialDESimulator(model,fs));
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

}
