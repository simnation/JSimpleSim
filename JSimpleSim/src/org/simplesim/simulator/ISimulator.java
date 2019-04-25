/**
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under GNU GPLv3.
 */
package org.simplesim.simulator;

import org.simplesim.core.scheduling.Time;

/**
 * @author Rene Kuhlemann
 *
 */
public interface ISimulator {

	void runSimulation(Time stop);

	Time getSimulationTime();

}
