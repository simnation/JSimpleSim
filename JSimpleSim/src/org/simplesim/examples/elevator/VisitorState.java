/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator;

import org.simplesim.examples.elevator.StaticVisitor.ACTIVITY;
import org.simplesim.model.State;

/**
 * The visitors state
 */
public final class VisitorState implements State {

	private int currentFloor;
	private ACTIVITY activity;

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int value) {
		currentFloor=value;
	}

	public ACTIVITY getActivity() {
		return activity;
	}

	public void setActivity(ACTIVITY value) {
		activity=value;
	}

}
