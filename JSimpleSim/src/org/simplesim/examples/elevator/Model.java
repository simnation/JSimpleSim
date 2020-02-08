/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractDomain;

/**
 *
 *
 */
public final class Model extends AbstractDomain {

	public final static int LOBBY=0;
	public final static int MAX_FLOOR=13;
	
	public static final int IDLE=0b00;
	public static final int DOWN=0b01;
	public static final int UP=0b10;
	
	/**
	 * Content of messages for elevator and visitor (submodels)
	 */
	static class Request {
		Visitor visitor;
		int startingFloor; // origin
		int destinationFloor; // destination floor
		Time requestTime; // time of request (button pressed in starting floor)
		Time arrivalTime; // time of arrival in destination floor

		boolean isGoingUp() {
			return startingFloor<destinationFloor;
		}

		boolean isGoingDown() {
			return startingFloor>destinationFloor;
		}
		
		int getDirection() {
			if (isGoingUp()) return UP;
			else if (isGoingDown()) return DOWN;
			return 0;
		}
	}

	public Model(int visitors) {
		final Elevator elevator=new Elevator(0);
		addEntity(elevator);
		for (int index=1; index<=visitors; index++) {
			final Visitor visitor=new Visitor(index);
			addEntity(visitor);
			elevator.addOutport(visitor).connectTo(visitor.getInport());
			visitor.getOutport().connectTo(elevator.getInport());
		}
	}
	
	@Override
	public String getName() {
		return "root";
	}

}
