/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.core;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 *
 *
 */
public interface Elevator {

	enum EVENT {
		idle, moved
	}

	void processMessages();

	void sendMessage(AbstractAgent<?,?> recipient, Request content);

	void enqueueEvent(EVENT event, Time time);

	ElevatorState getState();

	void log(Time time, String text);
}
