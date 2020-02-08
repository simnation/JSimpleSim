/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.DirectMessage;
import org.simplesim.core.routing.MultiPort;
import org.simplesim.core.routing.SinglePort;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

public final class Cell extends AbstractAgent<CellState, Object> {

	private final AbstractPort inport, outport;

	/**
	 * @param addr
	 */
	public Cell(int posX, int posY, boolean life) {
		super(null,new CellState());
		getState().setPosX(posX);
		getState().setPosY(posY);
		getState().setLife(life);
		inport=addInport(new SinglePort(this));
		outport=addOutport(new MultiPort(this));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.simplesim.model.AbstractAgent#doEvent(org.simplesim.core.scheduling.Time)
	 */
	@Override
	protected Time doEvent(Time time) {
		if (getInport().hasMessages()) {
			int neighbours=0;
			while (getInport().hasMessages()) if ((Boolean) getInport().poll().getContent()) neighbours++;
			final boolean life=(getState().isLife()&&(neighbours==2))||(neighbours==3);
			if (life!=getState().isLife()) getState().setLife(life);
		}
		getOutport().write(new DirectMessage(this,getState().isLife()));
		return null;
	}

	public AbstractPort getInport() {
		return inport;
	}

	public AbstractPort getOutport() {
		return outport;
	}

	@Override
	public String getName() {
		return "cell";
	}

}
