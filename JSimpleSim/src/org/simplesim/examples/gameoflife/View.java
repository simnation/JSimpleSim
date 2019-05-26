/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import static org.simplesim.examples.gameoflife.Application.BOARD_DX;
import static org.simplesim.examples.gameoflife.Application.BOARD_DY;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.simplesim.core.util.Listener;
import org.simplesim.model.AbstractAgent;
import org.simplesim.simulator.AbstractSimulator;

/**
 * 
 *
 */
@SuppressWarnings("serial")
public class View extends JFrame implements Listener<AbstractSimulator> {

	public static final int CELL_SIZE=5;

	public View(String title) {
		super(title);
		final Dimension size=new Dimension(CELL_SIZE*BOARD_DX,CELL_SIZE*BOARD_DY);
		setPreferredSize(size);
		setSize(size);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		System.out.println(getSize().toString());
	}

	@Override
	public void notifyListener(AbstractSimulator source) {
		final BufferStrategy bs=getBufferStrategy();
		do {
			do {
				// Get a new graphics context every time through the loop
				// to make sure the strategy is validated
				final Graphics g=bs.getDrawGraphics();
				for (final AbstractAgent<?, ?> cell : source.getCurrentEventList()) {
					final CellState cs=((Cell) cell).getState();
					if (cs.isLife()) g.setColor(Color.YELLOW);
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
