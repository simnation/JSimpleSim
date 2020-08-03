/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.dyn;

import static org.simplesim.examples.elevator.core.Limits.END_WORK;
import static org.simplesim.examples.elevator.core.Limits.IDLE_TIME;
import static org.simplesim.examples.elevator.core.Limits.LOBBY;
import static org.simplesim.examples.elevator.core.Limits.MAX_FLOOR;
import static org.simplesim.examples.elevator.core.Limits.START_DAY;
import static org.simplesim.examples.elevator.core.Limits.START_WORK;

import java.util.Random;

import org.simplesim.core.dynamic.DomainChangeRequest;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.core.Limits;
import org.simplesim.examples.elevator.core.Request;
import org.simplesim.examples.elevator.core.Visitor;
import org.simplesim.examples.elevator.core.VisitorState;
import org.simplesim.examples.elevator.core.VisitorState.ACTIVITY;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.RoutingAgent;

/**
 *
 *
 */
public final class DynamicVisitor extends RoutingAgent<VisitorState, Visitor.EVENT> implements Visitor {

	private static final Random random=new Random();
	private final DynamicModel building;

	public DynamicVisitor(DynamicModel model) {
		super(new VisitorState());
		building=model;
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

			// request model change to new floor
			final Floor dest=building.getFloor(request.getDestinationFloor());
			addModelChangeRequest(new DomainChangeRequest(this,dest));

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

	@Override
	public int getCurrentFloor() {
		return ((Floor) getParent()).getFloor();
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
		sendRequest(building.getElevator(),destination,time);
		getState().setActivity(ACTIVITY.waiting);
		getEventQueue().enqueue(EVENT.waiting,time.add(IDLE_TIME));
	}

	private void sendRequest(AbstractAgent<?, ?> dest, int destination, Time time) {
		final Request request=new Request(this,getCurrentFloor(),destination,time);
		final RoutedMessage msg=new RoutedMessage(this.getAddress(),dest.getAddress(),request);
		getOutport().write(msg); // send request to elevator
	}

	@Override
	public String getName() {
		return "DynamicVisitor";
	}

}
