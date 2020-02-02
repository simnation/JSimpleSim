/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.examples.elevator;

import java.util.LinkedList;
import java.util.List;

import org.simplesim.examples.elevator.Model.Request;
import org.simplesim.model.IAgentState;

/**
 * 
 *
 */
public final class ElevatorState implements IAgentState {

	private int currentFloor;
	private int destinationFloor;
	private int direction;
	final private List<Request> cabin=new LinkedList<>();
	@SuppressWarnings("unchecked")
	final private List<Request> queue[]=new List[Model.MAX_FLOOR+1]; // lobby=0, floor 1=1
	final private int button[]=new int[Model.MAX_FLOOR+1]; 
	
	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int value) {
		this.currentFloor=value;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public void setDestinationFloor(int value) {
		this.destinationFloor=value;
	}

	public List<Request> getCabin() {
		return cabin;
	}

	public List<Request> getQueue(int floor) {
		return queue[floor];
	}
	
	void setQueue(int floor, List<Request> value) {
		queue[floor]=value;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int value ) {
		this.direction = value;
	}

	public int getButton(int floor) {
		return button[floor];
	}
	
	void setButton(int floor,int value) {
		button[floor]=value;
	}
	
	public void updateButton(int floor,int value) {
		button[floor]|=value;
	}
	

}