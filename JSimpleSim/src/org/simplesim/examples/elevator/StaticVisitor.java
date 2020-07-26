/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import static org.simplesim.examples.elevator.Limits.END_WORK;
import static org.simplesim.examples.elevator.Limits.IDLE_TIME;
import static org.simplesim.examples.elevator.Limits.LOBBY;
import static org.simplesim.examples.elevator.Limits.MAX_FLOOR;
import static org.simplesim.examples.elevator.Limits.START_DAY;
import static org.simplesim.examples.elevator.Limits.START_WORK;

import java.util.Random;

import org.simplesim.core.messaging.AbstractPort;
import org.simplesim.core.messaging.DirectMessage;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 *
 *
 */
public final class StaticVisitor extends AbstractAgent<VisitorState, StaticVisitor.EVENT> {

	enum EVENT {
		changeFloor, waiting, goHome
	}

	enum ACTIVITY {
		waiting, working
	}

	private final AbstractPort inport, outport;
	private static final Random random=new Random();

	public StaticVisitor() {
		super(new VisitorState());
		inport=addInport(new SinglePort(this));
		outport=addOutport(new SinglePort(this));
		getState().setCurrentFloor(LOBBY);
		getState().setActivity(ACTIVITY.waiting);
		// init arrival time at lobby with a random value before start of work
		final Time time=START_DAY.add(random.nextInt((int) (START_WORK.getTicks()-START_DAY.getTicks())));
		getEventQueue().enqueue(EVENT.changeFloor,time);
	}

	@Override
	protected Time doEvent(Time time) {
		switch (getEventQueue().dequeue()) {
		case waiting:
			waitForElevator(time);
			break;
		case changeFloor:
			changeFloor(time);
			break;
		case goHome:
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
			getState().setCurrentFloor(request.getDestinationFloor()); // set new floor
			if ((time.compareTo(END_WORK)>=1)&&(request.getDestinationFloor()==LOBBY))
				getEventQueue().enqueue(EVENT.goHome,Time.INFINITY); // work is over, going home
			// go to another floor after staying here for a random time period
			else {
				getState().setActivity(ACTIVITY.working);
				getEventQueue().enqueue(EVENT.changeFloor,time.add(random.nextInt(Limits.MAX_STAY_TIME)));
			}
		} // else just wait a little longer
		else getEventQueue().enqueue(EVENT.waiting,time.add(IDLE_TIME));
	}

	/**
	 * Go randomly to an other floor
	 *
	 * @param time
	 */
	private void changeFloor(Time time) {
		int destination=getState().getCurrentFloor();
		if (time.compareTo(END_WORK)>=0) destination=LOBBY; // go to lobby after end of work
		else while (destination==getState().getCurrentFloor()) destination=1+random.nextInt(MAX_FLOOR);
		// create request
		final Request request=new Request(this,getState().getCurrentFloor(),destination,time);
		// send message to elevator, equals pushing the elevator button
		getOutport().write(new DirectMessage(this,request)); // send request to elevator
		getState().setActivity(ACTIVITY.waiting);
		getEventQueue().enqueue(EVENT.waiting,time.add(IDLE_TIME)); // wait for elevator
	}

	public AbstractPort getInport() {
		return inport;
	}

	public AbstractPort getOutport() {
		return outport;
	}

	@Override
	public String getName() {
		return "visitor";
	}

}
