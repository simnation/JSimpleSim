/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.scheduling.HeapBucketQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.View;
import org.simplesim.examples.elevator.stat.StaticElevator;
import org.simplesim.examples.elevator.stat.StaticModel;
import org.simplesim.examples.elevator.stat.StaticVisitor;
import org.simplesim.model.DirectMessageForwarding;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * Example of a multi-domain agent system with routed messaging and dynamic
 * model changes
 * <p>
 * To illustrate differences of a static and a dynamic modeling approach, both
 * are used with the same simulation problem: the steering strategy of an
 * elevator. Steering algorithm, graphical representation and common data structures are shared, 
 * so the focus lies on the differences of both approaches:
 * <p>
 * <u>Static model:</u>
 * <ul>
 * <li>There are no model changes.
 * <li>Visitors store their current floor as part of their state.
 * <li>Ports of elevator and visitor are connected directly.
 * <li>Direct message forwarding is used.
 * </ul>
 * <u>Dynamic model:</u>
 * <ul>
 * <li>Each floor is represented by a submodel containing its visitors.
 * <li>Change of the floor is implemented as moving to an other submodel, the model is changed repeatedly during simulation run.
 * <li>The model hierarchy Building-->Floor-->Visitor represents the real world situation comprehensibly.
 * <li>Messaging is done by a routing mechanism.
 * </ul>
 */
public class StaticMain {

	/**
	 * Example main method on how to set up a static simulation model
	 */
	public static void main(String[] args) {
		View.intro();

		final StaticModel model=new StaticModel();

		// build simulation model
		final StaticElevator elevator=model.getElevator();
		model.addEntity(elevator);
		for (int index=1; index<=Limits.VISITORS; index++) {
			final StaticVisitor visitor=new StaticVisitor();
			model.addEntity(visitor);
			elevator.connectTo(visitor);
			visitor.connectTo(elevator);
		}

		final View view=new View(elevator.getState());
		final Simulator simulator=new ConcurrentDESimulator(model,new HeapBucketQueue<>(),
				new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

}
