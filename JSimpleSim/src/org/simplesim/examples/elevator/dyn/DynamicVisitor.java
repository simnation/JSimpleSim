/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.dyn;

import static org.simplesim.examples.elevator.shared.Limits.END_WORK;
import static org.simplesim.examples.elevator.shared.Limits.IDLE_TIME;
import static org.simplesim.examples.elevator.shared.Limits.LOBBY;
import static org.simplesim.examples.elevator.shared.Limits.MAX_FLOOR;
import static org.simplesim.examples.elevator.shared.Limits.START_DAY;
import static org.simplesim.examples.elevator.shared.Limits.START_WORK;

import java.util.Random;

import org.simplesim.core.dynamic.DomainChangeRequest;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.Request;
import org.simplesim.examples.elevator.shared.Visitor;
import org.simplesim.examples.elevator.shared.VisitorState;
import org.simplesim.examples.elevator.shared.VisitorState.ACTIVITY;
import org.simplesim.model.BasicAgent;
import org.simplesim.model.RoutingAgent;

/**
 * Part of the dynamic elevator example
 *
 * @see org.simplesim.examples.elevator.DynamicMain DynamicMain
 *
 */
public final class DynamicVisitor extends RoutingAgent<VisitorState, Visitor.Event> implements Visitor {

	private static final Random random=new Random();
	private final DynamicModel building;

	public DynamicVisitor(DynamicModel model) {
		super(new VisitorState());
		building=model;
		getState().setActivity(ACTIVITY.waiting);
		// init arrival time at lobby with a random value before start of work
		final Time time=START_DAY.add(random.nextInt((int) (START_WORK.getTicks()-START_DAY.getTicks())));
		getEventQueue().enqueue(Event.CHANGE_FLOOR,time);
	}

	@Override
	public void doEvent(Time time) {
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
				getEventQueue().enqueue(Event.GO_HOME,Time.INFINITY); // work is over, going home
			// go to another floor after staying here for a random time period
			else {
				getState().setActivity(ACTIVITY.working);
				getEventQueue().enqueue(Event.CHANGE_FLOOR,time.add(random.nextInt(Limits.MAX_STAY_TIME)));
			}
		} // else just wait a little longer
		else getEventQueue().enqueue(Event.WAITING,time.add(IDLE_TIME));
	}

	@Override
	public int getCurrentFloor() { return ((Floor) getParent()).getFloor(); }

	@Override
	public Mood getCurrentMood(Time simTime) {
		final long diff=simTime.getTicks()-getState().getStartWaitingTime().getTicks();
		int index=(int) Math.floorDiv(diff,Limits.ACCEPTABLE_WAITING_TIME.getTicks());
		if (index>=Mood.values().length) index=Mood.values().length-1;
		return Mood.values()[index];
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
		getState().setStartWaitingTime(time);
		getEventQueue().enqueue(Event.WAITING,time.add(IDLE_TIME));
	}

	@Override
	public void sendRequest(BasicAgent<?, ?> dest, int destination, Time time) {
		final Request request=new Request(this,getCurrentFloor(),destination,time);
		final RoutedMessage msg=new RoutedMessage(this.getAddress(),dest.getAddress(),request);
		getOutport().write(msg); // send request to elevator
	}

	@Override
	public String getName() { return "DynamicVisitor"; }

}
