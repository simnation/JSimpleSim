/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.messaging.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractDomain;
import org.simplesim.simulator.SequentialDESimulator;
import org.simplesim.simulator.Simulator;

/**
 *
 *
 */
public final class StaticModelMain extends AbstractDomain {

	private final StaticElevator elevator=new StaticElevator();

	public StaticModelMain() {
		addEntity(elevator);
		for (int index=1; index<=Limits.VISITORS; index++) {
			final StaticVisitor visitor=new StaticVisitor();
			addEntity(visitor);
			elevator.addOutport(visitor).connectTo(visitor.getInport());
			visitor.getOutport().connectTo(elevator.getInport());
		}
	}
	
	public static void main(String[] args) {
		final StaticModelMain model=new StaticModelMain();
		final View view=new View(model.getElevator().getState());
		//final AbstractSimulator simulator=new ConcurrentDESimulator(model,new DirectMessageForwarding());
		final Simulator simulator=new SequentialDESimulator(model,new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(Limits.END_DAY));
		view.close();
	}
	
	public StaticElevator getElevator() {
		return elevator;
	}

	@Override
	public String getName() {
		return "root";
	}


}
