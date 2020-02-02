/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event
 * simulation.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 *
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.DirectMessage;
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
		inport=addSingleInport();
		outport=addMultiOutport();
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
			while (getInport().hasMessages()) if ((Boolean) getInport().read().getContent()) neighbours++;
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
