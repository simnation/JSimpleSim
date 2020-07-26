/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator2;

import java.util.Random;

import org.simplesim.core.dynamic.DomainChangeRequest;
import org.simplesim.core.messaging.RoutedMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.RoutingAgent;

/**
 *
 *
 */
public final class Visitor extends RoutingAgent<VisitorState, Visitor.EVENT> {

	enum EVENT {
		changeFloor, waiting, goHome
	}

	enum ACTIVITY {
		waiting, working
	}

	public final static String NAME="Visitor";

	private final static int START_DAY=1*Time.TICKS_PER_HOUR;
	private final static int START_WORK=2*Time.TICKS_PER_HOUR;
	private final static int END_WORK=9*Time.TICKS_PER_HOUR;
	private final static int MAX_SOJOURN_TIME=3*Time.TICKS_PER_HOUR;
	private final static int ACCEPTABLE_WAITING_TIME=3*Time.TICKS_PER_MINUTE;

	private final static Time WAITING_PERIOD=new Time(10*Time.TICKS_PER_SECOND);

	private final Random random=new Random();

	public Visitor() {
		super(new VisitorState());
		getState().setActivity(ACTIVITY.waiting);
		getState().setSatisfaction(0);
		final Time time=new Time(START_DAY+random.nextInt(START_WORK-START_DAY));
		getEventQueue().enqueue(EVENT.changeFloor,time);
	}

	@Override
	protected Time doEvent(Time time) {
		if (!getEventQueue().getMin().equals(time)) throw new RuntimeException("Time inconsistency!");
		final EVENT event=getEventQueue().dequeue();
		switch (event) {
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
			getState().setSatisfaction(assessTravelExperience(request)); // evaluate elevator ride
			final Floor dest=Building.getInstance().getFloor(request.destinationFloor);
			addModelChangeRequest(new DomainChangeRequest(this,dest));
			
			if ((time.getTicks()>=END_WORK)&&(request.destinationFloor==Building.LOBBY))
				getEventQueue().enqueue(EVENT.goHome,Time.INFINITY); // work is over, going home
			// go to another floor after staying here for a random time period
			else {
				getState().setActivity(ACTIVITY.working);
				getEventQueue().enqueue(EVENT.changeFloor,time.add(random.nextInt(MAX_SOJOURN_TIME)));
			}
		} // else just wait a little longer
		else getEventQueue().enqueue(EVENT.waiting,time.add(WAITING_PERIOD));
	}

	private int getCurrentLevel() {
		return ((Floor) getParent()).getFloor();
	}

	/**
	 * Go randomly to an other floor
	 *
	 * @param time
	 */
	private void changeFloor(Time time) {
		final Request request=new Request();
		request.visitor=this;
		request.requestTime=time; // time stamp for elevator button pressed
		request.arrivalTime=null; // no arrival yet
		request.startingFloor=request.destinationFloor=getCurrentLevel();
		// make sure to travel to an other floor
		while (request.destinationFloor==getCurrentLevel())
			request.destinationFloor=1+random.nextInt(Building.MAX_FLOOR);
		if (time.getTicks()>=END_WORK) request.destinationFloor=Building.LOBBY; // go to lobby after work
		// send message to elevator, equals pushing the elevator button
		sendMessage(getAddress(),Building.getInstance().getElevator().getAddress(),request);
		getState().setActivity(ACTIVITY.waiting);
		getEventQueue().enqueue(EVENT.waiting,time.add(WAITING_PERIOD)); // wait for elevator
	}

	/**
	 * Calculate a satisfaction score based on the overall waiting time
	 * <p>
	 * The score equivalents to the multiple of an acceptable traveling time. For example, a value of 2 means the real
	 * traveling time is approximately two times the acceptable traveling time.
	 * 
	 * @param requestTime time of pressing the elevator button
	 * @param arrivalTime arrival time in destination floor
	 */
	private int assessTravelExperience(Request request) {
		final int travelTime=(int) (request.arrivalTime.getTicks()-request.requestTime.getTicks());
		return Math.floorDiv(travelTime,ACCEPTABLE_WAITING_TIME);
	}

	public void sendMessage(int[] src, int[] dest, Request content) {
		getOutport().write(new RoutedMessage(src,dest,content));
	}

	@Override
	public String getName() {
		return NAME;
	}

}
