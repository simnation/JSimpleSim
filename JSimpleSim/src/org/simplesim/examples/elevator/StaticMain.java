/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.DirectMessageForwarding;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.View;
import org.simplesim.examples.elevator.shared.VisitorState;
import org.simplesim.examples.elevator.stat.StaticModel;
import org.simplesim.examples.elevator.stat.StaticVisitor;
import org.simplesim.model.InstrumentationDecorator;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.Simulator;

/**
 * Example of a multi-domain agent system with routed messaging and dynamic
 * model changes
 * <p>
 * To illustrate differences of a static and a dynamic modeling approach, both
 * are used with the same simulation problem: the steering strategy of an
 * elevator. Steering algorithm, graphical representation and common data
 * structures are shared, so the focus lies on the differences of both
 * approaches:
 * <p>
 * <u>Static model:</u>
 * <ul>
 * <li>There are no model changes during the simulation run.
 * <li>Visitors store their current floor as part of their state.
 * <li>Ports of elevator and visitor are connected directly.
 * <li>Direct message forwarding is used.
 * </ul>
 * <u>Dynamic model:</u>
 * <ul>
 * <li>Each floor is represented by a submodel containing its visitors.
 * <li>Change of the floor is implemented as moving to an other submodel, the
 * model is changed repeatedly during simulation run.
 * <li>The model hierarchy Building-->Floor-->Visitor represents the real world
 * situation comprehensibly.
 * <li>Messaging is done by a routing mechanism.
 * </ul>
 * Both approaches contain one agent whose state is observed by the
 * instrumentation feature.
 */
public class StaticMain {

	/**
	 * Example main method on how to set up a static simulation model
	 */
	public static void main(String[] args) {
		View.intro();

		final StaticModel building=new StaticModel(); // building already contains the elevator

		// add visitors
		for (int index=1; index<=Limits.VISITORS; index++) {
			final StaticVisitor visitor=new StaticVisitor();
			visitor.addToDomain(building);
			building.getElevator().getOutport().connect(visitor.getInport());
			visitor.getOutport().connect(building.getElevator().getInport());
		}

		addInstrumentedVisitor(building);

		final View view=new View(building.getElevator().getState());
		final Simulator simulator=new ConcurrentDESimulator(building,new HeapEventQueue<>(),
				new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

	private static void addInstrumentedVisitor(StaticModel building) {
		final InstrumentationDecorator<?, ?> instrumentedVisitor=new InstrumentationDecorator<>(new StaticVisitor());
		instrumentedVisitor.registerAfterExecutionListener((time, agent) -> {
			final VisitorState state=(VisitorState) agent.getState();
			if (state.getCurrentFloor()==state.getDestinationFloor())
				System.out.println(time.toString()+" agent arrived on floor "+state.getCurrentFloor());
			else System.out.println(
					time.toString()+" agent currently on floor "+state.getCurrentFloor()+" with destination floor "
							+state.getDestinationFloor()+", current mood is "+state.getCurrentMood(time).toString());
		});
		instrumentedVisitor.addToDomain(building);
		building.getElevator().getOutport().connect(instrumentedVisitor.getInport());
		instrumentedVisitor.getOutport().connect(building.getElevator().getInport());
	}

}
