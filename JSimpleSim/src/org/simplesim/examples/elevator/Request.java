/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.scheduling.Time;

/**
 * Content of messages for elevator and visitor (submodels)
 */
public class Request implements Comparable<Request> {
	
	// constant for elevator's direction
	public static final int IDLE=0b00;
	public static final int UP=0b10;
	public static final int DOWN=0b01;
	public static final int UPDOWN=UP|DOWN;
	
	private final StaticVisitor visitor;
	private final int startingFloor; // origin
	private final int destinationFloor; // destination floor
	private final Time requestTime; // time of request (button pressed in starting floor)
	private Time arrivalTime; // time of arrival in destination floor

	public Request(StaticVisitor v, int start, int dest, Time currTime) {
		visitor=v;
		startingFloor=start;
		destinationFloor=dest;
		requestTime=currTime;
		arrivalTime=null;
	}
	
	public StaticVisitor getVisitor() {
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
		if (isGoingUp()) return UP;
		else if (isGoingDown()) return DOWN;
		return IDLE;
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