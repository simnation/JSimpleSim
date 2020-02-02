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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.simplesim.model.AbstractAgent;
import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.DirectMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.Model.Request;
import static org.simplesim.examples.elevator.Model.UP;
import static org.simplesim.examples.elevator.Model.DOWN;
import static org.simplesim.examples.elevator.Model.IDLE;


/**
 * Elevator agent implementing a simple planning strategy
 * <p>
 * <ul>
 * <li> If there is any request in direction of movement with the same direction, go to the nearest one.
 * <li> If there is any other request in direction of movement, go to the farthest one.
 * <li> If there is no other request in direction of movement, change direction.
 * </ul>
 *
 */
public final class Elevator extends AbstractAgent<ElevatorState, Elevator.EVENT> {

	enum EVENT {
		idle, moved
	}
	
	public final static String NAME="Elevator";

	public final static int SPEED=2*Time.SECOND; // travel time to get from one floor to the next one
	public final static int DOOR_TIME=2*3*Time.SECOND; // time to open AND close the doors
	public final static int CHANGE_TIME=2*Time.SECOND; // time per visitor entering OR leaving the elevator

	private static final int CAPACITY=16; // maximum passenger capacity
	private static final int NONE=Integer.MIN_VALUE; // return value for no request found
	private static final int UPDOWN=UP|DOWN;
	private static final Time IDLE_TIME=new Time(30*Time.SECOND); // time to check for requests when idle

	private final AbstractPort inport;
	private final Map<Visitor, AbstractPort> outport=new HashMap<>(); // one outport per connected visitor

	public Elevator(int addr) {
		super(new ElevatorState());
		setAddress(new int[1]);
		getAddress()[0]=addr;
		inport=addSingleInport();
		for (int floor=Model.LOBBY; floor<=Model.MAX_FLOOR; floor++) {
			getState().setQueue(floor,new LinkedList<>());
			getState().setButton(floor,IDLE);
		}
		getState().setCurrentFloor(2);//Model.LOBBY);
		getState().setDestinationFloor(2);//Model.LOBBY);
		getState().setDirection(IDLE);
		getEventQueue().enqueue(EVENT.idle,IDLE_TIME);
	}

	@Override
	protected Time doEvent(Time time) {
		if (!getEventQueue().getMin().equals(time)) throw new RuntimeException("Time inconsistency!");
		final EVENT event=getEventQueue().dequeue();
		switch (event) {
		case moved: // just arrived on new floor
			getState().setCurrentFloor(getState().getDestinationFloor());
			prepareNextStep(time);
			break;
		case idle: // nothing to do, wait for passengers
			if (getInport().hasMessages()) prepareNextStep(time);
			else getEventQueue().enqueue(EVENT.idle,time.add(IDLE_TIME));
			break;
		default:
			throw new UnknownEventType("Unknown event type occured in "+getFullName());
		}
		return getTimeOfNextEvent();
	}

	/**
	 * Prepares the next movement of the elevator and evaluates all necessary
	 * information
	 *
	 * @param time              current time stamp
	 * @param exitingPassengers number of passengers that exited the cabin on the
	 *                          current floor
	 */
	private void prepareNextStep(Time time) {
		processMessages(); // any new requests?
		int exitingPassengers=exitCabin(time); // first, let passengers for this floor leave the cabin
		int enteringPassengers=enterCabin(time); // let new passengers enter the cabin going in current direction

		// there are request, so at least one button is pressed
		if (getState().getDirection()==IDLE) getState().setDirection(DOWN); // start search in downward direction

		int destination=calcNextDestination(); // calc floor of next stop
		if (destination==getState().getCurrentFloor()) { // no more request in current direction
			changeDirection();
			enteringPassengers=enterCabin(time); // let passengers enter for the opposite direction
			destination=calcNextDestination(); // calc floor of next stop again after changing direction
			//log(time,"New destination is now: "+destination);
		}
		getState().setDestinationFloor(destination);
		if (destination==getState().getCurrentFloor()) { // switch to idle state
			getState().setDirection(IDLE); // no request in any direction
			getEventQueue().enqueue(EVENT.idle,time.add(IDLE_TIME));
			return;
		}
		// travel time depends on number of changing passengers plus an offset...
		int travelTime=DOOR_TIME+(CHANGE_TIME*enteringPassengers)+(CHANGE_TIME*exitingPassengers);
		// ...and the number floors to move along
		travelTime+=SPEED*Math.abs(getState().getDestinationFloor()-getState().getCurrentFloor());
		getEventQueue().enqueue(EVENT.moved,time.add(travelTime));
		return;
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
		if (destination==getState().getCurrentFloor()) return NONE; // initial value not changed --> no requests
		return destination;
	}
	
	/**
	 * @return
	 */
	private int getNearestLowerRequest() {
		int result=Integer.MIN_VALUE;
		// anyone in the cabin going down?
		for (final Request request : getState().getCabin()) { 
			if ((request.destinationFloor<getState().getCurrentFloor())&&(request.destinationFloor>result))
				result=request.destinationFloor;
		}
		// anybody below pushed the down button?
		for (int floor=getState().getCurrentFloor()-1; floor>=Model.LOBBY; floor--) {
			if (isDownButtonPressed(floor)&&(floor>result)) {
				result=floor;
				break;

			}	
		}
		if (result==Integer.MIN_VALUE) return getState().getCurrentFloor();
		return result;
	}

	/**
	 * @return
	 */
	private int getNearestUpperRequest() {
		int result=Integer.MAX_VALUE; // set above limit, so any request should be lower
		// anyone in the cabin going up?
		for (final Request request : getState().getCabin()) { 
			if ((request.destinationFloor>getState().getCurrentFloor())&&(request.destinationFloor<result))
				result=request.destinationFloor; 
		}
		// anybody above pushed the up button?
		for (int floor=getState().getCurrentFloor()+1; floor<=Model.MAX_FLOOR; floor++) {
			if (isUpButtonPressed(floor)&&(floor<result)) {
				result=floor;
				break;
			}
		}
		if (result==Integer.MAX_VALUE) return getState().getCurrentFloor();
		return result;
	}

	private int findFarthestRequestOtherDirection() {
		if (getState().getDirection()==UP) { // look in the upper part for someone going DOWN
			for (int floor=Model.MAX_FLOOR; floor>getState().getCurrentFloor(); floor--) {
				if (isButtonPressed(floor)) return floor; // anybody above pushed the DOWN button?
			}
		} else if (getState().getDirection()==DOWN) { // look in the lower part for someone going down
			for (int floor=Model.LOBBY; floor<getState().getCurrentFloor(); floor++) {
				if (isButtonPressed(floor)) return floor; // anybody below pushed the UP button?
			}
		}
		return NONE;
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
		final int floor=getState().getCurrentFloor();
		final Iterator<Request> iter=getState().getQueue(floor).iterator();
		int counter=0;
		while_loop:
		while (iter.hasNext()) { // iterate over all passengers of the elevator cabin
			final Request request=iter.next();
			// transfer all passengers with same direction of travel as the elevator
			if (((request.destinationFloor>getState().getCurrentFloor())&&(getState().getDirection()==UP))
					||((request.destinationFloor<getState().getCurrentFloor())&&(getState().getDirection()==DOWN))) {
				if (getState().getCabin().size()>=CAPACITY) break while_loop; 
				getState().getCabin().add(request);
				iter.remove(); // transfer passenger from floor to cabin
				counter++;
				//log(time,request.visitor.getFullName()+" entered the cabin");
			}
		}
		int button=IDLE;
		for (Request request : getState().getQueue(floor)) button|=request.getDirection();
		getState().setButton(floor,button); // set button according to remaining requests
		return counter;
	}

	/**
	 * Let the passengers exit the elevator cabin
	 * <p>
	 * All passenger with current floor as destination are transfered from the cabin
	 * to the floor
	 *
	 * @param time time stamp of arrival
	 */
	private int exitCabin(Time time) {
		int counter=0;
		final Iterator<Request> iter=getState().getCabin().iterator();
		while (iter.hasNext()) { // go through each passenger of the elevator cabin
			final Request request=iter.next();
			if (request.destinationFloor==getState().getCurrentFloor()) {
				iter.remove(); // remove passenger from cabin if arrived at destination
				request.arrivalTime=time; // set time stamp of arrival
				// send direct message to inform about arrival
				getOutport(request.visitor).write(new DirectMessage(this,request));
				counter++;
				//log(time,request.visitor.getFullName()+" left the cabin");
			}
		}
		return counter;
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

	private void processMessages() {
		while (getInport().hasMessages()) {
			final Request request=getInport().read().getContent();
			final int floor=request.startingFloor;
			int direction=UP; // is passenger's destination up or down?
			if (request.isGoingDown()) direction=DOWN;
			getState().updateButton(floor,direction); // press outside elevator button
			getState().getQueue(floor).add(request); // add request to queue of starting floor
		}
	}

	public AbstractPort getInport() {
		return inport;
	}

	public AbstractPort getOutport(Visitor visitor) {
		return outport.get(visitor);
	}

	public AbstractPort addOutport(Visitor visitor) {
		final AbstractPort port=addSingleOutport();
		outport.put(visitor,port);
		return port;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

}
