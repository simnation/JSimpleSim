/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.model;

/**
 * Marker interface, the agent state contains the internal information of an {@code AbstractAgent}.<p>
 * An implementing class should contain all variables of an agent, getter and setter method as well as
 * a persistence technique to load and save the state. Additionally, there may be an observer pattern
 * or java bean functionality implemented to gain insight of the state during a simulation run.
 * 
 * @author Rene Kuhlemann
 *
 */
public interface IAgentState {
 
}
