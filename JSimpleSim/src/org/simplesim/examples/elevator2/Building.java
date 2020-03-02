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

import org.simplesim.examples.elevator2.Elevator;
import org.simplesim.model.RoutingDomain;

/**
 * 
 *
 */
public class Building extends RoutingDomain {
	
	public final static int LOBBY=0;
	public final static int MAX_FLOOR=13;
	
	private static final Building instance=new Building();
	private final Elevator elevator=new Elevator();
	
	private Building() {
		super();
		setAsRootDomain();
		addEntity(elevator);
	}
	
	public static Building getInstance() {
		return instance;
	}
	
	public Elevator getElevator() {
		return elevator;
	}
	
	public Floor getFloor(int i) {
		return (Floor) getDomainEntity(i+1); // first entry is always the elevator
	}
	
		
	@Override
	public String getName() {
		return "building";
	}

}
