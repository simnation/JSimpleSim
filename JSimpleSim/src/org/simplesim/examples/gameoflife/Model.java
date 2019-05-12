/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.examples.gameoflife;

import org.simplesim.examples.gameoflife.Model.GlobalBulletinBoard;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.IBulletinBoard;

/**
 * @author Rene Kuhlemann
 *
 */
public class Model extends AbstractDomain<GlobalBulletinBoard> {

	public class GlobalBulletinBoard implements IBulletinBoard {

		public int getDeltaX() {
			return board_dx;
		}

		public int getDeltaY() {
			return board_dy;
		}

	}

	private final Cell world[][];
	private final int board_dx, board_dy;

	public Model(int dx, int dy) {
		super("root");
		board_dx=dx;
		board_dy=dy;
		world=new Cell[dx][dy];
		setAddress(new int[0]);
		setBulletinBoard(new GlobalBulletinBoard());
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

}
