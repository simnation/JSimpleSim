/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.model.State;

public class CellState implements State {

	private int posX, posY; // the cell position
	private boolean life;   // is it alive?

	public int getPosX() { return posX; }

	public int getPosY() { return posY; }

	void setPosX(int x) { posX=x; }

	void setPosY(int y) { posY=y; }

	public boolean isLife() { return life; }

	public void setLife(boolean value) { life=value; }

}