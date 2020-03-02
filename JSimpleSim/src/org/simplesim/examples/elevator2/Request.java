/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator2;

import org.simplesim.core.scheduling.Time;

/**
 * Content of messages for elevator and visitor (submodels)
 */
class Request {
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
		if (isGoingUp()) return Elevator.UP;
		else if (isGoingDown()) return Elevator.DOWN;
		return 0;
	}
}