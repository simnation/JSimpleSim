/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.gameoflife;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.simplesim.core.instrumentation.Listener;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.Agent;
import org.simplesim.simulator.Simulator;

@SuppressWarnings("serial")
public class View extends JFrame implements Listener<Simulator> {

	private static final int CELL_SIZE=4;

	public View(String title, int width, int height) {
		super(title);
		final Dimension size=new Dimension(CELL_SIZE*width,CELL_SIZE*height);
		setPreferredSize(size);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
		createBufferStrategy(2);
	}

	@Override
	public void notifyListener(Time unused,Simulator source) {
		final BufferStrategy bs=getBufferStrategy();
		do {
			do {
				// Get a new graphics context every time through the loop
				// to make sure the strategy is validated
				final Graphics g=bs.getDrawGraphics();
				for (Agent cell : source.getCurrentEventList()) {
					final CellState cs=((Cell) cell).getState();
					if (cs.isAlive()) g.setColor(Color.YELLOW);
					else g.setColor(Color.BLUE);
					final int x=cs.getPosX()*CELL_SIZE;
					final int y=cs.getPosY()*CELL_SIZE;
					g.fillRect(x,y,CELL_SIZE,CELL_SIZE);
				}
				g.dispose();
				// Repeat the rendering if the drawing buffer contents
				// were restored
			} while (bs.contentsRestored());
			bs.show();
		} while (bs.contentsLost());
	}

	public void close() {
		setVisible(false);
		dispose();
	}

}
