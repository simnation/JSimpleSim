/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.ForwardingStrategy;
import org.simplesim.core.messaging.RoutedMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.core.Limits;
import org.simplesim.examples.elevator.core.View;
import org.simplesim.examples.elevator.dyn.DynamicElevator;
import org.simplesim.examples.elevator.dyn.DynamicModel;
import org.simplesim.examples.elevator.dyn.DynamicVisitor;
import org.simplesim.examples.elevator.dyn.Floor;
import org.simplesim.simulator.DynamicDecorator;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * Example of a multi-domain agent system with direct messaging
 * <p>
 * This
 */
public class DynamicMain {

	/**
	 * @param args
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
		// final Simulator simulator=new DynamicDecorator(new ConcurrentDESimulator(model,fs));
		final Simulator simulator=new DynamicDecorator(new SequentialDESimulator(model,fs));
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

}
