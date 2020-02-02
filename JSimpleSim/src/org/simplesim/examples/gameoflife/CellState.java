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

import org.simplesim.model.IAgentState;

public class CellState implements IAgentState {

	private int posX, posY;
	private boolean life;

	public CellState() {
	}

	/**
	 * @return the posX
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return the posY
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @param posY the posY to set
	 */
	void setPosX(int x) {
		posX=x;
	}

	/**
	 * @param posY the posY to set
	 */
	void setPosY(int y) {
		posY=y;
	}

	/**
	 * @return the life
	 */
	public boolean isLife() {
		return life;
	}

	/**
	 * @param life the life to set
	 */
	public void setLife(boolean newLife) {
		life=newLife;
	}
}