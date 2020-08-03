/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.examples.elevator.stat;

import org.simplesim.model.AbstractDomain;

/**
 *
 *
 */
public final class StaticModel extends AbstractDomain {

	/* init elevator, ensure only one elevator per model */
	private final StaticElevator elevator=new StaticElevator();
	
	public StaticElevator getElevator() {
		return elevator;
	}

	@Override
	public String getName() {
		return "root";
	}

}
