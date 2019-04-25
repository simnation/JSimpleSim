/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.List;

import org.simplesim.core.exceptions.InvalidPortException;
import org.simplesim.core.exceptions.NotUniqueException;
import org.simplesim.core.exceptions.OperationNotAllowedException;
import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.MultiPort;
import org.simplesim.core.routing.SinglePort;

/**
 *
 * @author Rene Kuhlemann
 */
public abstract class BasicModelEntity {

	private static final int INIT_LEVEL=-2;

	/**
	 * Flag to indicate if the simulation is running. true = simulation runs and
	 * changes of the model are forbidden
	 */
	private static boolean simulation_runs=false;

	/**
	 * Name of the entity, can be null
	 */
	private final String name;

	/** Address in numbers, describing the model's branch within the model tree */
	private int[] address;

	/** Parent entity in model hierarchy. */
	private AbstractDomain<?> parent=null;

	/**
	 * List of in and out ports. Each model can have an unlimited number of unique
	 * ports.
	 */
	private final List<AbstractPort> inports=new ArrayList<>(4);

	/** The out ports. */
	private final List<AbstractPort> outports=new ArrayList<>(4);

	/** The level in the hierarchy the model is located at. */
	private int level=INIT_LEVEL;

	@SuppressWarnings("serial")
	public static final class ModelChangeException extends RuntimeException {
		public ModelChangeException(String message) {
			super(message);
		}
	}

	/**
	 * Constructor
	 *
	 * @param name    of the entity
	 * @param address of the entity within the model hierarchy
	 */
	public BasicModelEntity(String n, int[] addr) {
		name=n;
		address=addr;
	}

	/**
	 * Adds a new input port to the model. Addition will fail if the same object is
	 * already used as a port.
	 *
	 * @param port the port to add
	 * @return returns a reference to the new port for further usage
	 */
	public final AbstractPort addInport(AbstractPort port) {
		return addPort(inports,port);
	}

	/**
	 * Creates and adds a new {@link SinglePort} as inport to the model.
	 * </p>
	 * Use this to create an inport in an {@code AbstractAgent} or building a
	 * connection into an {@code AbstractDomain}.
	 *
	 *
	 * @return returns a reference to the new port for further usage
	 */
	public final SinglePort addSingleInport() {
		return (SinglePort) addInport(new SinglePort(this));
	}

	/**
	 * Creates and adds a new {@link MultiPort} as inport to the model.
	 * </p>
	 * Using a {@code MultiPort} as an inport of an {@code AbstractDomain} and if
	 * messages have to be copied to various destinations at once
	 *
	 *
	 * @return returns a reference to the new port for further usage
	 */
	public final MultiPort addMultiInport() {
		return (MultiPort) addInport(new MultiPort(this));
	}

	/**
	 * Adds a new output port to the model. Addition will fail if the same object is
	 * already used as a port.
	 *
	 * @param port the port to add
	 * @return returns a reference to the new port for further usage
	 */
	public final AbstractPort addOutport(AbstractPort port) {
		return addPort(outports,port);
	}

	public final SinglePort addSingleOutport() {
		return (SinglePort) addOutport(new SinglePort(this));
	}

	public final MultiPort addMultiOutport() {
		return (MultiPort) addOutport(new MultiPort(this));
	}

	/**
	 * Adds a port to the model.
	 *
	 * @param portList the port list
	 * @param port     the port
	 * @return returns a reference to the port for further usage
	 */
	private AbstractPort addPort(List<AbstractPort> portList, AbstractPort port) {
		if (isSimulationRunning()) throw new OperationNotAllowedException(
				"Tried to add or remove a port during a simulation run in "+getFullName());
		if (portList.contains(port))
			throw new NotUniqueException("You are not allowed to add the same port twice to a model!");
		portList.add(port);
		return port;
	}

	/**
	 * Returns the number of in ports.
	 *
	 * @return number of inports
	 */
	public final int countInports() {
		return inports.size();
	}

	/**
	 * Gain access to the inports in an immutable way
	 *
	 * @return the inports
	 */
	public final Iterable<AbstractPort> getInports() {
		return inports;
	}

	/**
	 * Returns the number of out ports.
	 *
	 * @return number of outports
	 */
	public final int countOutports() {
		return outports.size();
	}

	/**
	 * Gain access to the outports in an immutable way
	 *
	 * @return the outports
	 */
	public final Iterable<AbstractPort> getOutports() {
		return outports;
	}

	public final void removeInports() {
		inports.clear();
	}

	public final void removeOutports() {
		outports.clear();
	}

	/**
	 * DEVS-like models are hierarchical models and thus each model resides on a
	 * level in the model tree. The level information is generated when the
	 * getLevel() method is called first. The level of the root node is alyways 0,
	 * the "no valid" level value is {@value #INIT_LEVEL}.
	 *
	 * @return the level of this model in the model hierarchy
	 */

	public final int getLevel() {
		// if there is no level information yet, re-compute it
		if (level==INIT_LEVEL) if (parent==null) level=0;
		else level=parent.getLevel()+1;
		return level;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the name
	 */
	public final int[] getAddress() {
		return address;
	}

	/**
	 * Returns the full name of a model A.B.C.name whereby A B and C are the names
	 * of the parent models and name is the name of this model
	 *
	 * @return the fullname
	 */

	public final String getFullName() {
		final String result;
		if (parent==null) result=toString();
		else {
			final StringBuilder sb=new StringBuilder(parent.getFullName());
			sb.append(".");
			sb.append(toString());
			result=sb.toString();
		}
		return result;
	}

	/**
	 * Returns the parent of this model.
	 *
	 * @return Returns the parent of this model
	 */

	public final AbstractDomain<?> getParent() {
		return parent;
	}

	/**
	 * Checks whether there is at least one input at any port.
	 *
	 * @return true if any inport has an input
	 */

	public final boolean hasExternalInput() {

		for (final AbstractPort port : getInports()) if (port.hasValue()) return true;
		// if we get here no port with a pending message has been found
		return false;
	}

	public final boolean hasInport(AbstractPort port) {
		return inports.contains(port);
	}

	public final boolean hasOutport(AbstractPort port) {
		return outports.contains(port);
	}

	/**
	 * Checks whether this model owns the given port or not.
	 *
	 * @param port which should be searched
	 *
	 * @return true if the port exists
	 */

	public final boolean hasPort(AbstractPort port) {
		return hasInport(port)||hasOutport(port);
	}

	/**
	 * Removes an existing input port from the model.
	 *
	 * @param port the port
	 */
	public final void removeInport(AbstractPort port) {
		removePort(inports,port);
	}

	/**
	 * Removes an existing input port from the model.
	 *
	 * @param port the port
	 */
	public final void removeOutport(AbstractPort port) {
		removePort(outports,port);
	}

	/**
	 * Removes a port (if this is allowed at invocation).
	 *
	 * @param portSet the port set
	 * @param port    the port
	 */
	private void removePort(List<AbstractPort> portSet, AbstractPort port) {
		if (simulation_runs) throw new OperationNotAllowedException(
				"Tried to add or remove a port during a simulation run in "+getFullName());
		if (!portSet.remove(port))
			throw new InvalidPortException("Tried to remove an unknown port from agent in "+getFullName());
	}

	public static final void toggleSimulationIsRunning(boolean toggle) {
		BasicModelEntity.simulation_runs=toggle;
	}

	public static final boolean isSimulationRunning() {
		return BasicModelEntity.simulation_runs;
	}

	/**
	 * Set the parent for this model.
	 *
	 * @param parent which should become the parent of this model
	 */
	final void setParent(AbstractDomain<?> parent) {
		this.parent=parent;
		// any updating which is related to setting a new parent must be done in
		// the reset method - this method is overwritten in descendant classes
		reset();
	}

	/**
	 * Set the address of this model.
	 *
	 * @param address as branching code
	 */
	public final void setAddress(int[] addr) {
		address=addr;
		reset();
	}

	/**
	 * Used internally for updating cached values if the structure changes (e.g.
	 * model is moved to another parent)
	 */
	public final void reset() {
		level=INIT_LEVEL;
	}

	/**
	 * Returns true if the two models have the same full name, means their are equal
	 * but not necessarily identical. For identity check use '=='
	 *
	 * @param model to be compared with this model
	 *
	 * @return true if the model and this model are identical
	 *
	 */
	@Override
	public boolean equals(Object other) {
		return this==other;
	}

	@Override
	public String toString() {
		if (name!=null) return name;
		else if (address!=null) return (Integer.toString(address[address.length-1]));
		return ("");
	}

}
