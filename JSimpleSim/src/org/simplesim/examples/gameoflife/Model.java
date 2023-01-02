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
	private final int width, height;

	public Model(int w, int h) {
		width=w;
		height=h;
		world=new Cell[width][height];
	}
	
	public void createCells(double lifeProbability) {
		for (int y=0; y<height; y++) for (int x=0; x<width; x++) {
			final Cell cell=new Cell(x,y,false);
			cell.getState().setLife(Math.random()<lifeProbability);
			world[x][y]=cell;
			addEntity(cell);
		}
	}
	
	public void connectCells() {
		for (int y=0; y<height; y++) for (int x=0; x<width; x++) {
			int left, right, up, down;
			if (x==0) left=width-1; else left=x-1;
			if (x==(width-1)) right=0; else right=x+1;
			if (y==0) down=height-1; else down=y-1;
			if (y==(height-1)) up=0; else up=y+1;
			// connect cell outport with inport of neighbor - clockwise
			final Cell cell=getCell(x,y);
			cell.connectTo(getCell(x,up));
			cell.connectTo(getCell(right,up));
			cell.connectTo(getCell(right,y));
			cell.connectTo(getCell(right,down));
			cell.connectTo(getCell(x,down));
			cell.connectTo(getCell(left,down));
			cell.connectTo(getCell(left,y));
			cell.connectTo(getCell(left,up));
		}
	}

	public Cell getCell(int x, int y) {
		return world[x][y];
	}

	public String getName() {
		return "world";
	}

}
