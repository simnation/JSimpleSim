/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator.shared;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 * Content of messages between elevator and visitor
 */
public class Request implements Comparable<Request> {
	
	private final AbstractAgent<?,?> visitor;
	private final int startingFloor; // origin
	private final int destinationFloor; // destination floor
	private final Time requestTime; // time of request (button pressed in starting floor)
	private Time arrivalTime; // time of arrival in destination floor

	public Request(AbstractAgent<?,?> v, int start, int dest, Time currTime) {
		visitor=v;
		startingFloor=start;
		destinationFloor=dest;
		requestTime=currTime;
		arrivalTime=null;
	}
	
	public AbstractAgent<?,?> getVisitor() {
		return visitor;
	}

	public int getStartingFloor() {
		return startingFloor;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public Time getRequestTime() {
		return requestTime;
	}

	public Time getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Time time) {
		arrivalTime=time;
	}

	public boolean isGoingUp() {
		return startingFloor<destinationFloor;
	}

	public boolean isGoingDown() {
		return startingFloor>destinationFloor;
	}
	
	public int getDirection() {
		if (isGoingUp()) return Limits.UP;
		else if (isGoingDown()) return Limits.DOWN;
		return Limits.IDLE;
	}
	
	public Time getTravelTime() {
		if (arrivalTime==null) return null;
		return arrivalTime.sub(requestTime);
	}
	
	public Time calcWaitingTime(Time currTime) {
		return currTime.sub(getRequestTime());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Request other) {
		return requestTime.compareTo(other.requestTime);
	}
	
	@Override
	public String toString() {
		return visitor.toString()+" going from "+startingFloor+" to "+destinationFloor;
	}
}