/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.core.scheduling.Time;

/**
 * 
 *
 */
public interface Elevator {
	
	enum EVENT { idle, moved }
	
	public void processMessages();
	
	public void sendMessage(StaticVisitor recipient, Request content);
	
	public void enqueueEvent(EVENT event, Time time);
	
	public ElevatorState getState();
	
	public void log(Time time,String text);
}
