/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import static org.simplesim.examples.elevator.shared.Limits.DOWN;
import static org.simplesim.examples.elevator.shared.Limits.IDLE;
import static org.simplesim.examples.elevator.shared.Limits.LOBBY;
import static org.simplesim.examples.elevator.shared.Limits.MAX_FLOOR;
import static org.simplesim.examples.elevator.shared.Limits.UP;
import static org.simplesim.examples.elevator.shared.Limits.UPDOWN;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Elevator.Event;

/**
 * Class encapsulating the elevator steering logic. 
 * <p>
 * The elevator follows one of the earliest strategies for elevator
 * control systems called <i>collective control</i>: Each floor is equipped with buttons representing the directions up
 * and down. The elevator cabin keeps track of all the calls made in the same direction, collecting all passengers going
 * in the same direction. The cabin then reverse and collects all passengers going in the opposite direction.
 * <p>This class only contains the steering logic, agent-specific functionality (messaging, event management,...) is part of the
 * elevator implementations. Thus, dynamic and static elevator both use the same strategy.
 */
public final class ElevatorStrategy {

	// constant for elevators destination
	private static final int NONE=Integer.MIN_VALUE;

	private final Elevator elevator;

	public ElevatorStrategy(Elevator e) {
		elevator=e;
	}

	/**
	 * Prepares the next movement of the elevator and evaluates all necessary information
	 *
	 * @param time current time stamp
	 */
	public void processMoveEvent(Time time) {
		elevator.processMessages(); // process any new requests

		final int exitingPassengers=exitCabin(time); // let passengers for this floor leave the cabin
		getState().setArrivals(getState().getCurrentFloor(),exitingPassengers);

		int enteringPassengers=enterCabin(time); // let new passengers going in current direction enter the cabin
		// if the elevator was idle, look for the next pressed button, start search in downward direction
		if (getState().getDirection()==IDLE) getState().setDirection(DOWN);

		int destination=calcNextDestination(); // calc floor of next stop
		if (destination==getState().getCurrentFloor()) { // no more requests for current direction?
			changeDirection(); // so, change direction and look again for requests
			enteringPassengers=enterCabin(time); // let passengers for the opposite direction enter the cabin
			destination=calcNextDestination(); // calc floor of next stop again after changing direction
		}

		if (destination==getState().getCurrentFloor()) { // no requests ==> switch to idle state
			getState().setDirection(IDLE); // no request in any direction
			getElevator().enqueueEvent(Event.IDLE,time.add(Limits.IDLE_TIME));
			return;
		}
		// elevator movement
		getState().setDestinationFloor(destination);
		// travel time depends on number of changing passengers plus an offset...
		int travelTime=Limits.DOOR_TIME+(Limits.CHANGE_TIME*(enteringPassengers+exitingPassengers));
		// ...and the number floors to move along
		travelTime+=Limits.SPEED*Math.abs(getState().getDestinationFloor()-getState().getCurrentFloor());
		elevator.enqueueEvent(Event.MOVED,time.add(travelTime));
	}

	/**
	 * @return
	 */
	private int calcNextDestination() {
		int destination=findNearestRequestSameDirection();
		if (destination==NONE) destination=findFarthestRequestOtherDirection();
		if (destination==NONE) destination=getState().getCurrentFloor();
		return destination;
	}

	private int findNearestRequestSameDirection() {
		int destination=getState().getCurrentFloor(); // propose no movement
		if (getState().getDirection()==UP) destination=getNearestUpperRequest();
		else if (getState().getDirection()==DOWN) destination=getNearestLowerRequest();
		if (destination==getState().getCurrentFloor()) destination=NONE; // initial value not changed --> no requests
		return destination;
	}

	private int findFarthestRequestOtherDirection() {
		if (getState().getDirection()==UP) { // look in the UPPER part for someone going DOWN
			for (int floor=MAX_FLOOR; floor>getState().getCurrentFloor(); floor--) {
				if (isButtonPressed(floor)) return floor; // anybody above pushed the DOWN button?
			}
		} else if (getState().getDirection()==DOWN) { // look in the LOWER part for someone going UP
			for (int floor=LOBBY; floor<getState().getCurrentFloor(); floor++) {
				if (isButtonPressed(floor)) return floor; // anybody below pushed the UP button?
			}
		}
		return NONE;
	}

	/**
	 * @return
	 */
	private int getNearestLowerRequest() {
		int destination=Integer.MIN_VALUE; // set below limit, so any request should be higher
		// anyone in the cabin going down?
		for (final Request request : getState().getCabin()) {
			if ((request.getDestinationFloor()<getState().getCurrentFloor())
					&&(request.getDestinationFloor()>destination))
				destination=request.getDestinationFloor();
		}
		// anybody below pushed the down button on a floor above the request of the the cabin?
		for (int floor=getState().getCurrentFloor()-1; floor>=LOBBY; floor--) {
			if (isDownButtonPressed(floor)&&(floor>destination)) return floor;
		}
		if (destination==Integer.MIN_VALUE) return getState().getCurrentFloor();
		return destination;
	}

	/**
	 * @return
	 */
	private int getNearestUpperRequest() {
		int destination=Integer.MAX_VALUE; // set above limit, so any request should be lower
		// anyone in the cabin going up?
		for (final Request request : getState().getCabin()) {
			if ((request.getDestinationFloor()>getState().getCurrentFloor())
					&&(request.getDestinationFloor()<destination))
				destination=request.getDestinationFloor();
		}
		// anybody above pushed the up button on a floor below the request of the the cabin?
		for (int floor=getState().getCurrentFloor()+1; floor<=MAX_FLOOR; floor++) {
			if (isUpButtonPressed(floor)&&(floor<destination)) return floor;
		}
		if (destination==Integer.MAX_VALUE) return getState().getCurrentFloor();
		return destination;
	}

	/**
	 * Let the passengers enter the elevator cabin
	 * <p>
	 * Takes cabin capacity and direction of travel into account.
	 * <p>
	 *
	 * @return number of passengers that entered the elevator cabin
	 */
	private int enterCabin(Time time) {
		final List<Request> enteringPassengers=new LinkedList<>();
		final int floor=getState().getCurrentFloor();

		for (final Request request : getState().getQueue(floor)) {
			if ((getState().getCabin().size()+enteringPassengers.size())>=Limits.CAPACITY) break;
			if (((request.getDestinationFloor()>floor)&&(getState().getDirection()==UP))
					||((request.getDestinationFloor()<floor)&&(getState().getDirection()==DOWN)))
				enteringPassengers.add(request);
		}
		getState().getCabin().addAll(enteringPassengers);
		getState().getQueue(floor).removeAll(enteringPassengers);
		int button=IDLE;
		for (final Request request : getState().getQueue(floor)) button|=request.getDirection();
		getState().setButton(floor,button); // set button according to remaining requests
		return enteringPassengers.size();
	}

	/**
	 * Let the passengers exit the elevator cabin
	 * <p>
	 * All passenger with current floor as destination are transfered from the cabin to the floor
	 *
	 * @param time time stamp of arrival
	 * @result number of passengers exiting the cabin on current floor
	 */
	private int exitCabin(Time time) {
		int count=0;
		final Iterator<Request> iter=getState().getCabin().iterator();
		while (iter.hasNext()) {
			final Request request=iter.next();
			if (request.getDestinationFloor()==getState().getCurrentFloor()) {
				request.setArrivalTime(time); // set time stamp of arrival
				getElevator().sendMessage(request.getVisitor(),request); // send direct message to inform about arrival
				iter.remove(); // remove passenger from cabin
				count++;
			}
		}
		return count;
	}

	private boolean isUpButtonPressed(int floor) {
		return (getState().getButton(floor)&UP)==UP;
	}

	private boolean isDownButtonPressed(int floor) {
		return (getState().getButton(floor)&DOWN)==DOWN;
	}

	private boolean isButtonPressed(int floor) {
		return getState().getButton(floor)!=IDLE;
	}

	private void changeDirection() {
		getState().setDirection(getState().getDirection()^UPDOWN);
	}

	private Elevator getElevator() {
		return elevator;
	}

	private ElevatorState getState() {
		return elevator.getState();
	}

}
