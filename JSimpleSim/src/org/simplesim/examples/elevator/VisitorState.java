/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.examples.elevator;

import org.simplesim.examples.elevator.Visitor.ACTIVITY;
import org.simplesim.model.IAgentState;

/**
 * The visitors state
 *
 */
public final class VisitorState implements IAgentState {
	
	private int currentLevel;
	private int satisfaction;
	private ACTIVITY activity;
	
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(int value) {
		this.currentLevel = value;
	}

	public int getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(int value) {
		this.satisfaction = value;
	}

	public ACTIVITY getActivity() {
		return activity;
	}

	public void setActivity(ACTIVITY value) {
		this.activity = value;
	}

	

}
