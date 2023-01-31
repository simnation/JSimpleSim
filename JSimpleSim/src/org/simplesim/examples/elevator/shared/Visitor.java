/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator.shared;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.Agent;
import org.simplesim.model.BasicAgent;

/**
 * Basic interface to encapsulate visitor functionality and events
 *
 */
public interface Visitor extends Agent {

	enum Event {
		CHANGE_FLOOR, WAITING, GO_HOME
	}

	void sendRequest(BasicAgent<?, ?> dest, int destination, Time time);

}
