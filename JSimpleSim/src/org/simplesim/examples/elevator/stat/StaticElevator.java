/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.stat;

import static org.simplesim.examples.elevator.shared.Limits.LOBBY;
import static org.simplesim.examples.elevator.shared.Limits.START_DAY;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.messaging.SwitchPort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.examples.elevator.shared.Elevator;
import org.simplesim.examples.elevator.shared.ElevatorState;
import org.simplesim.examples.elevator.shared.ElevatorStrategy;
import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.examples.elevator.shared.Request;
import org.simplesim.model.AbstractAgent;

/**
 * Part of the static elevator example
 *
 * @see org.simplesim.examples.elevator.StaticMain StaticMain
 */
public final class StaticElevator extends AbstractAgent<ElevatorState, Elevator.Event> implements Elevator {

	private final ElevatorStrategy strategy;

	public StaticElevator() {
		super(new ElevatorState());
		setInport(new SinglePort(this));
		setOutport(new SwitchPort(this));
		strategy=new ElevatorStrategy(this);
		getState().setCurrentFloor(LOBBY);
		getState().setDestinationFloor(LOBBY);
		getState().setDirection(Limits.IDLE);
		enqueueEvent(Event.IDLE,START_DAY);
	}

	@Override
	protected Time doEvent(Time time) {
		switch (getEventQueue().dequeue()) {
		case MOVED: // just arrived on new floor
			getState().setCurrentFloor(getState().getDestinationFloor());
			strategy.processMoveEvent(time);
			break;
		case IDLE: // nothing to do, wait for passengers
			if (getInport().hasMessages()) strategy.processMoveEvent(time);
			else enqueueEvent(Event.IDLE,time.add(Limits.IDLE_TIME));
			break;
		default:
			throw new UnknownEventType("Unknown event type occured in ElevatorStrategy");
		}
		return getTimeOfNextEvent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.examples.elevator.Elevator#processMessages()
	 */
	@Override
	public void processMessages() {
		while (getInport().hasMessages()) {
			final Request request=getInport().poll().getContent();
			getState().pressButton(request.getStartingFloor(),request.getDirection()); // press outside elevator button
			getState().getQueue(request.getStartingFloor()).add(request); // add request to queue of starting floor
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.examples.elevator.Elevator#enqueueEvent(org.simplesim.examples.elevator.Visitor,
	 * org.simplesim.examples.elevator.Request)
	 */
	@Override
	public void sendMessage(AbstractAgent<?, ?> recipient, Request content) {
		getOutport().write(new Message<AbstractAgent<?, ?>>(this,recipient,content));
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.examples.elevator.Elevator#enqueueEvent(org.simplesim.examples.elevator.Elevator.EVENT,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueueEvent(Event event, Time time) {
		getEventQueue().enqueue(event,time);
	}

	@Override
	public String getName() {
		return "Elevator";
	}

}
