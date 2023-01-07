/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.State;

/**
 * Class containing all relevant variables of the visitor state
 */
public final class VisitorState implements State {
	
	public enum ACTIVITY {
		waiting, working
	}
	
	private ACTIVITY activity;
	private Time startWaitingTime=Time.ZERO;
	

	public ACTIVITY getActivity() {
		return activity;
	}

	public void setActivity(ACTIVITY value) {
		activity=value;
	}

	public Time getStartWaitingTime() { return startWaitingTime; }

	public void setStartWaitingTime(Time value) { startWaitingTime=value; }

}
