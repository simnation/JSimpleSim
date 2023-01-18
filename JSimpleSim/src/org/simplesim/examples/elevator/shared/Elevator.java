/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.BasicAgent;

/**
 * Basic interface to encapsulate elevator functionality and events
 *
 */
public interface Elevator {

	enum Event {
		IDLE, MOVED
	}

	void processMessages();

	void sendMessage(BasicAgent<?,?> recipient, Request content);

	void enqueueEvent(Event event, Time time);

	ElevatorState getState();

}
