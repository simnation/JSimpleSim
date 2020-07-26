/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

/**
 * The state contains the internal information of an {@code AbstractAgent}.
 * <p>
 * Marker interface. A state serves to decouple logic and data. It should bundle all necessary information of an agent. It should support
 * persistence in a way that the complete model state is saved when all states are saved.
 * <p>
 * An implementing class should only contain variables, getter and setter methods - no other methods or logic functions.
 * <p>
 * A state may also implement an observer pattern or java bean functionality to gain insight of the state
 * during a simulation run.
 * <p>
 * Note: a state can also be used by domains or other classes to bundle variables and facilitate persistence.
 */
public interface State {

}
