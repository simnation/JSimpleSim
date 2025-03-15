/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable and used JSimpleSim as technical
 * backbone for concurrent discrete event simulation. This software is published as open source and licensed under GNU
 * GPLv3. Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.examples.elevator.dyn;

import org.simplesim.examples.elevator.shared.Limits;
import org.simplesim.model.RoutingDomain;

/**
 * Part of the dynamic elevator example
 * 
 * @see org.simplesim.examples.elevator.DynamicMain DynamicMain 
 * 
 */
public class DynamicModel extends RoutingDomain {

	private final DynamicElevator elevator=new DynamicElevator();

	public DynamicModel() {
		super();
		setAsRootDomain();
		addEntity(elevator); // add elevator
		addEntity(new Floor(Limits.LOBBY)); // add lobby 
	}

	public DynamicElevator getElevator() {
		return elevator;
	}

	public Floor getFloor(int i) {
		return (Floor) listDomainEntities().get(i+1); // first entry is always the elevator
	}
	
	public Floor getLobby() {
		return getFloor(Limits.LOBBY);
	}
		

	@Override
	public String getName() {
		return "building";
	}

}
