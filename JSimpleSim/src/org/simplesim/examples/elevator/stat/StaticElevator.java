/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.stat;

import static org.simplesim.examples.elevator.core.Limits.LOBBY;
import static org.simplesim.examples.elevator.core.Limits.START_DAY;

import java.util.HashMap;
import java.util.Map;

import org.simplesim.core.messaging.AbstractPort;
import org.simplesim.core.messaging.DirectMessage;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;
import org.simplesim.model.BasicModelEntity;
import org.simplesim.examples.elevator.core.Elevator;
import org.simplesim.examples.elevator.core.ElevatorState;
import org.simplesim.examples.elevator.core.ElevatorStrategy;
import org.simplesim.examples.elevator.core.Limits;
import org.simplesim.examples.elevator.core.Request;



/**
 * Elevator agent implementing a simple planning strategy
 * <ul>
 * <li>If there is any request in direction of movement with the same direction, go to the nearest one.
 * <li>If there is any other request in direction of movement, go to the farthest one.
 * <li>If there is no other request in direction of movement, change direction.
 * </ul>
 */
public final class StaticElevator extends AbstractAgent<ElevatorState, Elevator.EVENT> implements Elevator {

	private final ElevatorStrategy strategy;
	private final AbstractPort inport;
	private final Map<BasicModelEntity, AbstractPort> outport=new HashMap<>(); // one outport per connected visitor

	public StaticElevator() {
		super(new ElevatorState());
		inport=addInport(new SinglePort(this));
		getState().setCurrentFloor(LOBBY);
		getState().setDestinationFloor(LOBBY);
		getState().setDirection(Limits.IDLE);
		strategy=new ElevatorStrategy(this);
		enqueueEvent(EVENT.idle,START_DAY);
	}

	@Override
	protected Time doEvent(Time time) {
		switch (getEventQueue().dequeue()) {
		case moved: // just arrived on new floor
			getState().setCurrentFloor(getState().getDestinationFloor());
			strategy.processMoveEvent(time);
			break;
		case idle: // nothing to do, wait for passengers
			if (getInport().hasMessages()) strategy.processMoveEvent(time);
			else enqueueEvent(EVENT.idle,time.add(Limits.IDLE_TIME));
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
	public void sendMessage(AbstractAgent<?,?> recipient, Request content) {
		getOutport(recipient).write(new DirectMessage(this,content));
	}

	/*
	 * (non-Javadoc)
	 * @see org.simplesim.examples.elevator.Elevator#enqueueEvent(org.simplesim.examples.elevator.Elevator.EVENT,
	 * org.simplesim.core.scheduling.Time)
	 */
	@Override
	public void enqueueEvent(EVENT event, Time time) {
		getEventQueue().enqueue(event,time);
	}

	public AbstractPort getInport() {
		return inport;
	}

	public AbstractPort getOutport(BasicModelEntity visitor) {
		return outport.get(visitor);
	}

	public AbstractPort addOutport(BasicModelEntity visitor) {
		final AbstractPort port=addOutport(new SinglePort(this));
		outport.put(visitor,port);
		return port;
	}

	@Override
	public String getName() {
		return "Elevator";
	}

}
