/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import static org.simplesim.examples.gameoflife.Application.CELL_SIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Rene Kuhlemann
 *
 */
public class View extends JPanel implements Observer {

	private static final long serialVersionUID=1L;

	

	private final Model model;

	public View(Model m) {
		model=m;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		final Graphics g=this.getGraphics();
		final CellState cs=(CellState) o;
		if (cs.isLife()) g.setColor(Color.YELLOW);
		else g.setColor(Color.BLUE);
		final int x=cs.getPosX()*CELL_SIZE;
		final int y=cs.getPosY()*CELL_SIZE;
		g.fillRect(x,y,CELL_SIZE,CELL_SIZE);
	}

}
