/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.messaging.Message;
import org.simplesim.core.messaging.MultiPort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.BasicAgent;

/**
 * Agent representing a cell of the grid. 
 */
public final class Cell extends BasicAgent<CellState, Object> {

	public Cell(int posX, int posY, boolean life) {
		super(null,new CellState());
		getState().setPosX(posX);
		getState().setPosY(posY);
		getState().setAlive(life);
		setOutport(new MultiPort(this));
	}

	@Override
	public Time doEvent(Time time) {
		if (getInport().hasMessages()) {
			int neighbours=0;
			while (getInport().hasMessages()) {
				if ((Boolean) getInport().poll().getContent()) neighbours++;
			}
			if ((getState().isAlive()&&(neighbours==2))||(neighbours==3)) getState().setAlive(true);
			else getState().setAlive(false);
		}
		getOutport().write(new Message(this,getState().isAlive()));
		return null;
	}

	@Override
	public String getName() { return "cell"; }

}
