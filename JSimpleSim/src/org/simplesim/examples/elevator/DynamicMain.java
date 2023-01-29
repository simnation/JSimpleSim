/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.MessageForwardingStrategy;
import org.simplesim.core.messaging.RecursiveMessageForwarding;
import org.simplesim.core.scheduling.HeapEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.dyn.DynamicModel;
import org.simplesim.examples.elevator.dyn.DynamicVisitor;
import org.simplesim.examples.elevator.dyn.Floor;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.View;
import org.simplesim.examples.elevator.shared.VisitorState;
import org.simplesim.model.InstrumentationDecorator;
import org.simplesim.simulator.ConcurrentDESimulator;
import org.simplesim.simulator.DynamicDecorator;
import org.simplesim.simulator.SequentialDESimulator;
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
 * <li>There are no model changes.
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
 */
public class DynamicMain {

	/**
	 * Example main method on how to set up a dynamic simulation model
	 */
	public static void main(String[] args) {
		View.intro();

		// build the root of the simulation model
		final DynamicModel building=new DynamicModel(); // building already includes elevator and lobby

		// add floors
		for (int floor=1; floor<=Limits.MAX_FLOOR; floor++) new Floor(floor).addToDomain(building);

		// add one instrumented visitor to lobby
		createInstrumentedVisitor().addToDomain(building.getLobby());

		// place new visitors in lobby
		for (int i=0; i<Limits.VISITORS; i++) new DynamicVisitor().addToDomain(building.getLobby());

		final View view=new View(building.getElevator().getState());
		final MessageForwardingStrategy fs=new RecursiveMessageForwarding();
		//final Simulator simulator=new DynamicDecorator(new ConcurrentDESimulator(building,new HeapEventQueue<>(),fs));
		final Simulator simulator=new DynamicDecorator(new SequentialDESimulator(building,new HeapEventQueue<>(),fs));
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}

	private static InstrumentationDecorator<?, ?> createInstrumentedVisitor() {
		final InstrumentationDecorator<?, ?> id=new InstrumentationDecorator<>(new DynamicVisitor());
		id.registerAfterExecutionListener((time, agent) -> {
			final VisitorState state=(VisitorState) agent.getState();
			if (state.getCurrentFloor()==state.getDestinationFloor())
				System.out.println(time.toString()+" agent arrived on floor "+state.getCurrentFloor());
			else System.out.println(
					time.toString()+" agent currently on floor "+state.getCurrentFloor()+" with destination floor "
							+state.getDestinationFloor()+", current mood is "+state.getCurrentMood(time).toString());
		});
		return id;
	}
}
