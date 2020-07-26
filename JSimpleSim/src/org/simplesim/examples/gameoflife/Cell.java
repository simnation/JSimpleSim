/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.messaging.AbstractPort;
import org.simplesim.core.messaging.DirectMessage;
import org.simplesim.core.messaging.MultiPort;
import org.simplesim.core.messaging.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

public final class Cell extends AbstractAgent<CellState, Object> {

	private final AbstractPort inport, outport;

	public Cell(int posX, int posY, boolean life) {
		super(null,new CellState());
		getState().setPosX(posX);
		getState().setPosY(posY);
		getState().setLife(life);
		inport=addInport(new SinglePort(this));
		outport=addOutport(new MultiPort(this));
	}

	@Override
	protected Time doEvent(Time time) {
		if (getInport().hasMessages()) {
			int neighbours=0;
			while (getInport().hasMessages()) {
				if ((Boolean) getInport().poll().getContent()) neighbours++;
			}
			if ((getState().isLife()&&(neighbours==2))||(neighbours==3)) getState().setLife(true);
			else getState().setLife(false);
		}
		getOutport().write(new DirectMessage(this,getState().isLife()));
		return null;
	}

	public AbstractPort getInport() { return inport; }

	public AbstractPort getOutport() { return outport; }

	@Override
	public String getName() { return "cell"; }

}
