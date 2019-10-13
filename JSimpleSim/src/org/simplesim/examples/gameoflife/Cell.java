/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.DirectMessage;
import org.simplesim.core.scheduling.IEventQueue;
import org.simplesim.core.scheduling.Time;
import org.simplesim.model.AbstractAgent;

/**
 * @author Rene Kuhlemann
 *
 */
public final class Cell extends AbstractAgent<CellState, Object> {

	private final AbstractPort inport, outport;

	/**
	 * @param addr
	 */
	public Cell(int posX, int posY, boolean life) {
		super();
		getState().setPosX(posX);
		getState().setPosY(posY);
		getState().setLife(life);
		inport=addSingleInport();
		outport=addMultiOutport();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.model.AbstractAgent#createState()
	 */
	@Override
	protected CellState createState() {
		return new CellState();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.simplesim.model.AbstractAgent#createInternalEventQueue()
	 */
	@Override
	protected IEventQueue<Object> createLocalEventQueue() {
		return null; // time step simulation --> no local event queue
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

	public String getName() {
		return "cell";
	}

}
