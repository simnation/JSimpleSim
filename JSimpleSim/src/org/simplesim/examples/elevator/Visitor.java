/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import java.util.Random;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.SinglePort;
import org.simplesim.core.routing.DirectMessage;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.Model.Request;
import org.simplesim.model.AbstractAgent;

/**
 * 
 *
 */
public final class Visitor extends AbstractAgent<VisitorState, Visitor.EVENT> {

	enum EVENT {
		changeFloor, waiting, goHome
	}
	
	enum ACTIVITY { waiting, working }
	
	public final static String NAME="Visitor";

	private final static int START_DAY=8*Time.HOUR;
	private final static int START_WORK=9*Time.HOUR;
	private final static int END_WORK=17*Time.HOUR;
	private final static int MAX_SOJOURN_TIME=4*Time.HOUR;
	private final static int ACCEPTABLE_WAITING_TIME=3*Time.MINUTE;
	
	private final static Time WAITING_PERIOD=new Time(10*Time.SECOND);

	private final AbstractPort inport, outport;
	private final Random random=new Random();

	public Visitor(int addr) {
		super(new VisitorState());
		setAddress(new int[1]);
		getAddress()[0]=addr;
		inport=addInport(new SinglePort(this));
		outport=addOutport(new SinglePort(this));
		getState().setCurrentLevel(Model.LOBBY);
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
			getState().setCurrentLevel(request.destinationFloor); // set new floor
			getState().setSatisfaction(assessTravelExperience(request)); // evaluate elevator ride
			if ((time.getTicks()>=END_WORK)&&(getState().getCurrentLevel()==Model.LOBBY)) 
				getEventQueue().enqueue(EVENT.goHome,Time.INFINITY); // work is over, going home
			// go to another floor after staying here for a random time period
			else {
				getState().setActivity(ACTIVITY.working);
				getEventQueue().enqueue(EVENT.changeFloor,time.add(random.nextInt(MAX_SOJOURN_TIME)));
			}
		} // else just wait a little longer 
		else getEventQueue().enqueue(EVENT.waiting,time.add(WAITING_PERIOD));
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
		request.startingFloor=request.destinationFloor=getState().getCurrentLevel();
		// make sure to travel to an other floor
		while (request.destinationFloor==getState().getCurrentLevel())
			request.destinationFloor=1+random.nextInt(Model.MAX_FLOOR);
		if (time.getTicks()>=END_WORK) request.destinationFloor=Model.LOBBY; // go to lobby after work
		// send message to elevator, equals pushing the elevator button
		getOutport().write(new DirectMessage(this,request)); // send request to elevator
		getState().setActivity(ACTIVITY.waiting);
		getEventQueue().enqueue(EVENT.waiting,time.add(WAITING_PERIOD)); // wait for elevator
	}

	/**
	 * Calculate a satisfaction score based on the overall waiting time
	 * <p>
	 * The score equivalents to the multiple of an acceptable traveling time. For example, a
	 * value of 2 means the real traveling time is approximately two times the acceptable traveling time.
	 *  
	 * @param requestTime time of pressing the elevator button
	 * @param arrivalTime arrival time in destination floor
	 */
	private int assessTravelExperience(Request request) {
		final int travelTime=(int) (request.arrivalTime.getTicks()-request.requestTime.getTicks());
		return Math.floorDiv(travelTime,ACCEPTABLE_WAITING_TIME);
	}

	public AbstractPort getInport() {
		return inport;
	}

	public AbstractPort getOutport() {
		return outport;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

}
