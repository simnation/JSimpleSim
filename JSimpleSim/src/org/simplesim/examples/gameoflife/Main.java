/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.messaging.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.ConcurrentTSSimulator;
import org.simplesim.simulator.SequentialTSSimulator;
import org.simplesim.simulator.Simulator;

public final class Main {

	public static final int GRID_DX=300;
	public static final int GRID_DY=200;

	private static final double LIFE_PROBABILITY=0.35d;

	public static void main(String[] args) {
		final Model model=new Model(GRID_DX,GRID_DY);
		model.createCells(LIFE_PROBABILITY);
		model.connectCells();
		final View view=new View("JSimpleSim exmaple: Conway's Game of Life",GRID_DX,GRID_DY);
		final Simulator simulator=new SequentialTSSimulator(model,new DirectMessageForwarding());
		// final Simulator simulator=new ConcurrentTSSimulator(model,new DirectMessageForwarding());
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(Time.INFINITY);
		view.close();
	}

}
