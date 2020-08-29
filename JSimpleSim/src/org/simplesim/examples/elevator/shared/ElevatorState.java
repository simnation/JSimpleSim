/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simplesim.model.State;

/**
 *	Class containing all relevant variables of the elevator state 
 */
public final class ElevatorState implements State {

	private int currentFloor;
	private int destinationFloor;
	private int direction; // moving direction
	private final List<Request> cabin=new LinkedList<>(); // who is in the cabin?
	private final List<Queue<Request>> queueList=new ArrayList<>(); // requests grouped by floor
	private final int arrivals[]=new int[Limits.MAX_FLOOR+1]; // number of recent arrival for graphic view
	private final int button[]=new int[Limits.MAX_FLOOR+1]; // status of elevator button on each floor

	public ElevatorState() {
		for (int floor=0; floor<=Limits.MAX_FLOOR; floor++) {
			queueList.add(new LinkedList<Request>());
			setArrivals(floor,0);
			setButton(floor,Limits.IDLE);
		}
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int value) {
		currentFloor=value;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public void setDestinationFloor(int value) {
		destinationFloor=value;
	}

	public List<Request> getCabin() {
		return cabin;
	}

	public Queue<Request> getQueue(int floor) {
		return queueList.get(floor);
	}

	public void addToQueue(int floor, Request request) {
		queueList.get(floor).add(request);
	}

	public void removeFromQueue(int floor, Request request) {
		queueList.get(floor).remove(request);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int value) {
		direction=value;
	}

	public int getArrivals(int floor) {
		return arrivals[floor];
	}

	public void setArrivals(int floor, int value) {
		arrivals[floor]=value;
	}

	public int getButton(int floor) {
		return button[floor];
	}

	public void setButton(int floor, int value) {
		button[floor]=value;
	}

	public void pressButton(int floor, int value) {
		button[floor]|=value;
	}

}
