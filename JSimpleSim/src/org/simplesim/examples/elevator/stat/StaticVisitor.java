/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.stat;

import static org.simplesim.examples.elevator.shared.Limits.END_WORK;
import static org.simplesim.examples.elevator.shared.Limits.IDLE_TIME;
import static org.simplesim.examples.elevator.shared.Limits.LOBBY;
import static org.simplesim.examples.elevator.shared.Limits.MAX_FLOOR;
import static org.simplesim.examples.elevator.shared.Limits.START_DAY;
import static org.simplesim.examples.elevator.shared.Limits.START_WORK;

import java.util.Random;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.Request;
import org.simplesim.examples.elevator.shared.Visitor;
import org.simplesim.examples.elevator.shared.VisitorState;
import org.simplesim.examples.elevator.shared.VisitorState.ACTIVITY;
import org.simplesim.model.AbstractAgent;

/**
 * Part of the static elevator example
 *
 * @see org.simplesim.examples.elevator.StaticMain StaticMain
 */
public final class StaticVisitor extends AbstractAgent<VisitorState, Visitor.Event> implements Visitor {

	private static final Random random=new Random();
	private int currentFloor=LOBBY; // usually, this should be a state variable

	public StaticVisitor() {
		super(new VisitorState());
		setOutport(new SinglePort(this));
		getState().setActivity(ACTIVITY.waiting);
		// init arrival time at lobby with a random value before start of work
		final Time time=START_DAY.add(random.nextInt((int) (START_WORK.getTicks()-START_DAY.getTicks())));
		getEventQueue().enqueue(Event.CHANGE_FLOOR,time);
	}

	@Override
	public Time doEvent(Time time) {
		switch (getEventQueue().dequeue()) {
		case WAITING:
			waitForElevator(time);
			break;
		case CHANGE_FLOOR:
			changeFloor(time);
			break;
		case GO_HOME:
			throw new RuntimeException("Never should get here!");
		default:
			throw new UnknownEventType("Unknown event type occured in "+toString());
		}
		return getTimeOfNextEvent();
	}

	/**
	 * @param time
	 */
	private void waitForElevator(Time time) {
		if (getInport().hasMessages()) {
			// message from elevator agent: visitor arrived at destination floor
			final Request request=getInport().poll().getContent();
			setCurrentFloor(request.getDestinationFloor()); // set new floor
			if ((time.compareTo(END_WORK)>=1)&&(request.getDestinationFloor()==LOBBY))
				getEventQueue().enqueue(Event.GO_HOME,Time.INFINITY); // work is over, going home
			// go to another floor after staying here for a random time period
			else {
				getState().setActivity(ACTIVITY.working);
				getEventQueue().enqueue(Event.CHANGE_FLOOR,time.add(random.nextInt(Limits.MAX_STAY_TIME)));
			}
		} // else just wait a little longer
		else getEventQueue().enqueue(Event.WAITING,time.add(IDLE_TIME));
	}

	/**
	 * Go randomly to an other floor
	 *
	 * @param time
	 */
	private void changeFloor(Time time) {
		int destination=getCurrentFloor();
		if (time.compareTo(END_WORK)>=0) destination=LOBBY; // go to lobby after end of work
		else while (destination==getCurrentFloor()) destination=1+random.nextInt(MAX_FLOOR);
		sendRequest(null,destination,time);
		getState().setActivity(ACTIVITY.waiting);
		getState().setStartWaitingTime(time);
		getEventQueue().enqueue(Event.WAITING,time.add(IDLE_TIME)); // wait for elevator
	}

	@Override
	public void sendRequest(AbstractAgent<?, ?> dest, int destination, Time time) {
		final Request request=new Request(this,getCurrentFloor(),destination,time);
		getOutport().write(new Message(this,request)); // send request to elevator
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.examples.elevator.core.Visitor#getCurrentFloor()
	 */
	@Override
	public int getCurrentFloor() {
		return currentFloor;
	}

	private void setCurrentFloor(int value) {
		currentFloor=value;
	}

	@Override
	public String getName() {
		return "visitor";
	}

}
