/**
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU
 * GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.List;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.MultiPort;
import org.simplesim.core.routing.SinglePort;

/**
 * Provides basic functionality needed by all entities within the simulation
 * model.
 * <p>
 * In more detail, that is:
 * <ul>
 * <li>unit identification (by name or address)
 * <li>managing in- and outports
 * <li>utility methods (model level, naming, {@code toString}, {@code equals})
 * </ul>
 *
 */
public abstract class BasicModelEntity {

	private static final int INIT_LEVEL=-2;

	/**
	 * Flag to indicate if the simulation is running. true = simulation runs and
	 * changes of the model are forbidden
	 */
	private static boolean simulation_runs=false;

	/** Address in numbers, describing the model's branch within the model tree */
	private int[] address;

	/** Parent entity in model hierarchy. */
	private AbstractDomain parent=null;

	/**
	 * List of in and out ports. Each model can have an unlimited number of unique
	 * ports.
	 */
	private final List<AbstractPort> inports=new ArrayList<>(4);

	/** The out ports. */
	private final List<AbstractPort> outports=new ArrayList<>(4);

	/** The level in the hierarchy the model is located at. */
	private int level=INIT_LEVEL;
	
	/**
	 * Exception to be thrown if an invalid operation occurs when adding or removing a port.
	 */
	@SuppressWarnings("serial")
	public static class InvalidPortException extends RuntimeException {
		public InvalidPortException(String message) {
			super(message);
		}
	}
	
	/** 
	 * Exception to be thrown if a duplicate object is used where only a unique one is allowed.
	 */
	@SuppressWarnings("serial")
	public static class UniqueConstraintViolation extends RuntimeException {
	  public UniqueConstraintViolation(String message) {
	    super(message);
	  }
	}

	/**
	 * Constructor taking the address as initial parameter.
	 *
	 * @param address the address of the entity within the model hierarchy, may be null
	 */
	public BasicModelEntity(int[] addr) {
		address=addr;
	}
	
	public BasicModelEntity() {
		this(null);
	}

	/**
	 * Adds a new inport to the model.
	 *
	 * @param port the port to add
	 * @return reference to the new port for further usage
	 *
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final AbstractPort addInport(AbstractPort port) {
		return addPort(inports,port);
	}

	/**
	 * Creates and adds a new {@code SinglePort} as inport to the model.
	 * <p>
	 * Use this to create an inport for an {@code AbstractAgent} or to build a
	 * connection into an {@code AbstractDomain}.
	 *
	 * @return reference to the new port for further usage
	 *
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final SinglePort addSingleInport() {
		return (SinglePort) addInport(new SinglePort(this));
	}

	/**
	 * Creates and adds a new {@code MultiPort} as inport to the model.
	 * <p>
	 * Note: This only makes sense if the entity is an {@code AbstractDomain} and if
	 * messages have to be copied to various destinations within this domain
	 *
	 * @return reference to the new port for further usage
	 * 
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final MultiPort addMultiInport() {
		return (MultiPort) addInport(new MultiPort(this));
	}

	/**
	 * Adds a new outport to the model.
	 * <p>
	 * Addition will fail if the same object is already used as a port.
	 *
	 * @param port the port to add
	 * @return reference to the new port for further usage
	 *
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final AbstractPort addOutport(AbstractPort port) {
		return addPort(outports,port);
	}

	/**
	 * Creates and adds a new {@code SinglePort} as outport to the model.
	 * <p>
	 * Use this to create an outport for an {@code AbstractAgent} or to build a
	 * connection into an {@code AbstractDomain}.
	 *
	 * @return reference to the new port for further usage
	 * 
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final SinglePort addSingleOutport() {
		return (SinglePort) addOutport(new SinglePort(this));
	}

	/**
	 * Creates and adds a new {@code MultiPort} as outport to the model.
	 * <p>
	 * This enables copying messages to various destinations at once.
	 *
	 * @return reference to the new port for further usage
	 * 
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws NotUniqueException           if the same port is added twice
	 */
	public final MultiPort addMultiOutport() {
		return (MultiPort) addOutport(new MultiPort(this));
	}

	/** private utility method to add port to port lists */
	private AbstractPort addPort(List<AbstractPort> portList, AbstractPort port) {
		if (isSimulationRunning())
			throw new UnsupportedOperationException("Tried to add a port during a simulation run in "+getFullName());
		if (portList.contains(port)) throw new UniqueConstraintViolation("A port may not be added twice to the same model!");
		portList.add(port);
		return port;
	}

	/**
	 * Returns the number of inports.
	 *
	 * @return number of inports
	 */
	public final int countInports() {
		return inports.size();
	}

	/**
	 * Provides access to iterate over the inports in an immutable way
	 *
	 * @return inport iterator
	 */
	public final Iterable<AbstractPort> getInports() {
		return inports;
	}

	/**
	 * Returns the number of outports.
	 *
	 * @return number of outports
	 */
	public final int countOutports() {
		return outports.size();
	}

	/**
	 * Provides access to iterate over the outports in an immutable way
	 *
	 * @return outport iterator
	 */
	public final Iterable<AbstractPort> getOutports() {
		return outports;
	}

	/**
	 * Removes all inports from this entity
	 * <p>
	 *
	 * Note: Ports have to be disconnected before! There is no check for a
	 * simulation run! Use of this method may crash the simulation!
	 */
	public final void removeInports() {
		inports.clear();
	}

	/**
	 * Removes all outports from this entity
	 * <p>
	 *
	 * Note: Ports have to be disconnected before! There is no check for a
	 * simulation run! Use of this method may crash the simulation!
	 */
	public final void removeOutports() {
		outports.clear();
	}

	/**
	 * Models may be stacked in a hierarchy and thus each entity resides in a domain
	 * level of the model tree. The level information is generated when the
	 * getLevel() method is called first. The level of the root node is always 0,
	 * the "no valid" level value is {@value #INIT_LEVEL}.
	 *
	 * @return the level of this entity in the model hierarchy
	 */
	public final int getLevel() {
		// if there is no level information yet, re-compute it
		if (level==INIT_LEVEL) if (parent==null) level=0;
		else level=parent.getLevel()+1;
		return level;
	}

	/**
	 * Gets the entity address. Can be null.
	 * <p>
	 *
	 * Note: The address of the root domain is {@code int[0]}. Another dimension has
	 * to be added per model level. The value of each dimension is the index within
	 * the corresponding level.
	 *
	 * @return the address
	 */
	public final int[] getAddress() {
		return address;
	}

	/**
	 * Returns the full name of a model, concatenating the names of the parent
	 * entities.
	 * <p>
	 * Example: If A and B are parents of this entity and this entity is named C,
	 * then the full name is A.B.C
	 *
	 * @return the full name of this entity
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
	 * @return the parent of this model
	 */
	public final AbstractDomain getParent() {
		return parent;
	}

	/**
	 * Checks whether there is at least one input at any inport.
	 *
	 * @return true if any inport has an input
	 */
	public final boolean hasExternalInput() {
		for (final AbstractPort port : getInports()) if (port.hasMessages()) return true;
		return false; // if we get here no port with a pending message has been found
	}

	/**
	 * Checks whether the given port is an inport of this entity.
	 *
	 * @param port the port to be tested
	 * @return true if the port is an inport of this entity
	 */
	public final boolean hasInport(AbstractPort port) {
		return inports.contains(port);
	}

	/**
	 * Checks whether the given port is an outport of this entity.
	 *
	 * @param port the port to be tested
	 * @return true if the port is an outport of this entity
	 */
	public final boolean hasOutport(AbstractPort port) {
		return outports.contains(port);
	}

	/**
	 * Checks whether the given port is either an in- or an outport of this entity
	 *
	 * @param port the port to be tested
	 *
	 * @return true if the port is inport or outport in this entity
	 */
	public final boolean hasPort(AbstractPort port) {
		return hasInport(port)||hasOutport(port);
	}

	/**
	 * Removes an existing inport from the model.<br>
	 *
	 * @param port the port
	 *
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws InvalidPortException         if the port is unknown
	 */
	public final void removeInport(AbstractPort port) {
		removePort(inports,port);
	}

	/**
	 * Removes an existing outport from the model.<br>
	 *
	 * @param port the port
	 *
	 * @throws OperationNotAllowedException if the simulation is running
	 * @throws InvalidPortException         if the port is unknown
	 */
	public final void removeOutport(AbstractPort port) {
		removePort(outports,port);
	}

	private void removePort(List<AbstractPort> portList, AbstractPort port) {
		if (simulation_runs)
			throw new UnsupportedOperationException("Tried to remove a port during a simulation run in "+getFullName());
		if (!portList.remove(port))
			throw new InvalidPortException("Tried to remove an unknown port from agent in "+getFullName());
	}

	/**
	 * Sets the status of simulation run.<br>
	 * Static method and variable to be accessible for all entities of the
	 * simulation model
	 *
	 * @param toggle the status of the simulation, {@code true} means simulation is
	 *               running
	 */
	public static final void toggleSimulationIsRunning(boolean toggle) {
		BasicModelEntity.simulation_runs=toggle;
	}

	/**
	 * Gets the status of simulation run.<br>
	 * Static method and variable to be accessible for all entities of the
	 * simulation model
	 *
	 * @return current simulation status, {@code true} means simulation is running
	 */
	public static final boolean isSimulationRunning() {
		return BasicModelEntity.simulation_runs;
	}

	/**
	 * Sets the parent for this model.
	 * <p>
	 *
	 * Note: Using this during a simulation run may crash the simulation!
	 *
	 * @param parent which should become the parent of this model
	 */
	final void setParent(AbstractDomain par) {
		parent=par;
		// any updating which is related to setting a new parent must be done in
		// the reset method - this method is overwritten in descendant classes
		reset();
	}

	/**
	 * Sets the address of this model. Should only be used internally when changing
	 * the model.
	 * <p>
	 * Note: The address of the root domain is {@code int[0]}. Another dimension has
	 * to be added per model level. The value of each dimension is the index within
	 * the corresponding level.
	 *
	 * @param addr address as branching code
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
	 * Tests if two model entities are equal (equality by identity)
	 *
	 * @param other entity to be compared with this one
	 *
	 * @return true if both entities are identical
	 *
	 */
	@Override
	public boolean equals(Object other) {
		return this==other;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (address!=null) return (Integer.toString(address[address.length-1]));
		return ("");
	}

}
