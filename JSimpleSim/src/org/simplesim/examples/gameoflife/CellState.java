/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
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