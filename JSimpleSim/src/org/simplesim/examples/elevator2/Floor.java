/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.examples.elevator2;

import org.simplesim.model.RoutingDomain;

/**
 * 
 *
 */
public final class Floor extends RoutingDomain {
	
	private final int floor;
	
	public Floor(int l) {
		super();
		floor=l;
	}
	
	public int getFloor() {
		return floor;
	}
	
	@Override
	public String getName() {
		return "Floor";
	}

}
