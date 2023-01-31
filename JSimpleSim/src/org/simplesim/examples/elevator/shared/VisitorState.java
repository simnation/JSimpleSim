/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.shared;

import java.awt.Color;

import org.simplesim.core.scheduling.Time;
import org.simplesim.model.State;

/**
 * Class containing all relevant variables of the visitor state
 */
public final class VisitorState implements State {
	
	public enum ACTIVITY {
		waiting, working
	}
	
	public enum Mood {
		GOOD(Color.GREEN, "good"), NERVOUS(Color.YELLOW, "nervous"), AGITATED(Color.ORANGE, "agitated"),
		ANGRY(Color.RED, "angry");
	
		final Color color;
		final String str;
	
		Mood(Color c, String s) {
			color=c;
			str=s;
		}
	
		public Color getColor() { return color; }
	
		@Override
		public String toString() { return str; }
	
	}

	private ACTIVITY activity;
	private Time startWaitingTime=Time.ZERO;
	private int currFloor=0;
	private int destFloor=0;
	

	public ACTIVITY getActivity() {
		return activity;
	}
	
	public void setActivity(ACTIVITY value) {
		activity=value;
	}

	public Time getStartWaitingTime() { return startWaitingTime; }

	public void setStartWaitingTime(Time value) { startWaitingTime=value; }

	public int getCurrentFloor() { return currFloor; }

	public void setCurrentFloor(int currFloor) { this.currFloor = currFloor; }

	public int getDestinationFloor() { return destFloor; }

	public void setDestinationFloor(int destFloor) { this.destFloor = destFloor; }
	
	public Mood getCurrentMood(Time simTime) {
		final long diff=simTime.getTicks()-getStartWaitingTime().getTicks();
		int index=(int) Math.floorDiv(diff,Limits.ACCEPTABLE_WAITING_TIME.getTicks());
		if (index>=Mood.values().length) index=Mood.values().length-1;
		return Mood.values()[index];
	}

}
