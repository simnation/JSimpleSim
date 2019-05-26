/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.routing.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.AbstractSimulator;
import org.simplesim.simulator.ConcurrentTSSimulator;
import org.simplesim.simulator.SequentialTSSimulator;

/**
 * @author Rene Kuhlemann
 *
 */
public final class Application {

	public static final int BOARD_DX=300;
	public static final int BOARD_DY=200;

	private static final double LIFE_PROBABILITY=0.3d;

	private static void initModel(Model model) {
		// connect cells
		for (int y=0; y<BOARD_DY; y++) for (int x=0; x<BOARD_DX; x++) {
			int left, right, up, down;
			if (x==0) left=BOARD_DX-1;
			else left=x-1;
			if (x==(BOARD_DX-1)) right=0;
			else right=x+1;
			if (y==0) down=BOARD_DY-1;
			else down=y-1;
			if (y==(BOARD_DY-1)) up=0;
			else up=y+1;
			// connect cell outport with inport of neighbour - clockwise
			final Cell cell=model.getCell(x,y);
			cell.getOutport().connectTo(model.getCell(x,up).getInport());
			cell.getOutport().connectTo(model.getCell(right,up).getInport());
			cell.getOutport().connectTo(model.getCell(right,y).getInport());
			cell.getOutport().connectTo(model.getCell(right,down).getInport());
			cell.getOutport().connectTo(model.getCell(x,down).getInport());
			cell.getOutport().connectTo(model.getCell(left,down).getInport());
			cell.getOutport().connectTo(model.getCell(left,y).getInport());
			cell.getOutport().connectTo(model.getCell(left,up).getInport());
			// set life status randomly
			cell.getState().setLife(Math.random()<LIFE_PROBABILITY);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Model model=new Model(BOARD_DX,BOARD_DY);
		initModel(model);
		final View view=new View("JSimpleSim exmaple: Conway's Game of Life");
		view.createBufferStrategy(2);
		//final AbstractSimulator simulator=new SequentialTSSimulator(model,new DirectMessageForwarding());
		final AbstractSimulator simulator=new ConcurrentTSSimulator(model,new DirectMessageForwarding());
		// add observer
		simulator.registerEventsProcessedListener(view);
		simulator.runSimulation(new Time(3000));
		view.close();
	}

}
