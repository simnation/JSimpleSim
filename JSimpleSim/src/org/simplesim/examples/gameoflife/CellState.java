/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import java.util.Observable;

import org.simplesim.model.AbstractState;

/**
 * @author Rene Kuhlemann
 */
public class CellState extends Observable implements AbstractState {

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
		setChanged();
		notifyObservers(this);
	}
}