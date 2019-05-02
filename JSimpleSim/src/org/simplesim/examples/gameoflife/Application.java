/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.simplesim.core.routing.DirectMessageForwarding;
import org.simplesim.core.scheduling.Time;
import org.simplesim.simulator.ISimulator;
import org.simplesim.simulator.TimeStepSimulator;

/**
 * @author Rene Kuhlemann
 *
 */
public final class Application {

	public static final int BOARD_DX=300;
	public static final int BOARD_DY=200;
	
	public static final int CELL_SIZE=5;

	private static final double LIFE_PROBABILITY=0.3d;
	
	final JFrame frame=new JFrame("JSimpleSim exmaple: Conway's Game of Life");

	private void initModel(Model model, View view) {
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
			// add observer
			cell.getState().addObserver(view);
			// set life status randomly
			cell.getState().setLife(Math.random()<LIFE_PROBABILITY);
		}
	}

	private void runSimulation(Model model, Time stop) {
		final ISimulator simulator=new TimeStepSimulator(model,new DirectMessageForwarding());
		simulator.runSimulation(stop);
	}
	
	private void initAndShowGUI(View view) {
		final Dimension size=new Dimension(CELL_SIZE*BOARD_DX,CELL_SIZE*BOARD_DY);
		view.setPreferredSize(size);
		view.setSize(size);
		frame.add(view);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		System.out.println(view.getSize().toString());
	}
	
	/**
	 * 
	 */
	private void close() {
		frame.setVisible(false);
	    frame.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Model model=new Model(BOARD_DX,BOARD_DY);
		final View view=new View(model);
		final Application app=new Application();
		app.initAndShowGUI(view);
		app.initModel(model,view);
		app.runSimulation(model,new Time(3000));
		app.close();
	}

}