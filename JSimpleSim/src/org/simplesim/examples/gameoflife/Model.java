/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.model.AbstractDomain;


public class Model extends AbstractDomain {

	private final Cell world[][];

	public Model(int dx, int dy) {
		super();
		world=new Cell[dx][dy];
		setAddress(new int[0]);
		// create cells and add to model ==> no connection yet!
		for (int y=0; y<dy; y++) for (int x=0; x<dx; x++) {
			final Cell cell=new Cell(x,y,false);
			world[x][y]=cell;
			addEntity(cell);
		}
	}

	public Cell getCell(int x, int y) {
		return world[x][y];
	}

	public String getName() {
		return "world";
	}

}
