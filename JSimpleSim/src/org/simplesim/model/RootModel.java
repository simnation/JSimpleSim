/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.model;

import org.simplesim.model.RootModel.GlobalInfoBoard;

/**
 * @author Rene Kuhlemann
 *
 */
public class RootModel extends AbstractDomain<GlobalInfoBoard> {
	
	static class GlobalInfoBoard implements IInfoBoard {
		
	}

	private final static RootModel instance=new RootModel();

	
	private RootModel() {
		super("root");
		setAddress(new int[0]);
	}

	public static RootModel getInstance() {
		return instance;
	}

}
