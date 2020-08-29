/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.List;

import org.simplesim.core.messaging.AbstractPort;

/**
 * Provides basic functionality needed by all entities within the simulation model.
 * <p>
 * In more detail, that is:
 * <ul>
 * <li>unit identification (by name or address)
 * <li>managing in- and outports
 * <li>utility methods (model level, naming, {@code toString}, {@code equals})
 * <li>providing relevant exceptions 
 * </ul>
 */
public abstract class BasicModelEntity {

	public static final int ROOT_LEVEL=0;
	private static final int INIT_LEVEL=Integer.MIN_VALUE;
	private static final int DEFAULT_PORT_LIST_SIZE=1;

	/** Parent entity in model hierarchy. */
	private AbstractDomain parent=null;

	/** List of inports */
	private List<AbstractPort> inports=null;

	/** List of outports */
	private List<AbstractPort> outports=null;

	/** Address in numbers, describing the model's branch within the model tree */
	private int[] address=null;

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
	public static class UniqueConstraintViolationException extends RuntimeException {
		public UniqueConstraintViolationException(String message) {
			super(message);
		}
	}

	/**
	 * Adds a new inport to the model.
	 *
	 * @param <P>  class of port
	 * @param port the port to add
	 * @return reference to the new port for further usage
	 * @throws UnsupportedOperationException      if the simulation is running
	 * @throws UniqueConstraintViolationException if the same port is added twice
	 */
	@SuppressWarnings("unchecked")
	public final <P extends AbstractPort> P addInport(P port) {
		if (inports==null) setInportList(new ArrayList<>(DEFAULT_PORT_LIST_SIZE));
		return (P) addPort(inports,port);
	}

	/**
	 * Adds a new outport to the model.
	 * <p>
	 * Addition will fail if the same object is already used as a port.
	 *
	 * @param <P>  class of port
	 * @param port the port to add
	 * @return reference to the new port for further usage
	 * @throws UnsupportedOperationException      if the simulation is running
	 * @throws UniqueConstraintViolationException if the same port is added twice
	 */
	@SuppressWarnings("unchecked")
	public final <P extends AbstractPort> P addOutport(P port) {
		if (outports==null) setOutportList(new ArrayList<>(DEFAULT_PORT_LIST_SIZE));
		return (P) addPort(outports,port);
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
	 * Looks up an indexed port from the inport list.
	 *
	 * @param index the of the port in the list of inports
	 * @return the indexed inport
	 */
	public final AbstractPort getInport(int index) {
		return inports.get(index);
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
	 * Looks up an indexed port from the outport list.
	 *
	 * @param index the of the port in the list of inports
	 * @return the indexed outport
	 */
	public final AbstractPort getOutport(int index) {
		return outports.get(index);
	}

	/**
	 * Returns the level of the current domain within the model hierarchy
	 * <p>
	 * Models may be organized in a hierarchy, so that each entity resides in a definite domain level of the model tree.
	 * The level information is generated when the getLevel() method is called first. The level of the root node is
	 * always {@value #ROOT_LEVEL}, the "no valid" level value is {@value #INIT_LEVEL}.
	 *
	 * @return the level of this entity in the model hierarchy
	 */
	public final int getLevel() {
		// if there is no level information yet, re-compute it
		if (level==INIT_LEVEL) {
			if (parent==null) level=ROOT_LEVEL;
			else level=parent.getLevel()+1;
		}
		return level;
	}

	/**
	 * Gets the entity address. Can be null.
	 * <p>
	 * Note: The address of the root domain is {@code int[0]}. Another dimension has to be added per model level. The
	 * value of each dimension is the index within the corresponding level.
	 *
	 * @return the address
	 */
	public final int[] getAddress() {
		return address;
	}

	/**
	 * Returns the name of this model entity
	 * <p>
	 * Returns an empty string as default, may be overridden in derived classes.
	 *
	 * @return the name of this model entity, may be an empty string but not null
	 */
	public String getName() {
		return "";
	}

	/**
	 * Returns the full name of a model, concatenating the names of the parent entities.
	 * <p>
	 * Example: If A and B are parents of this entity and this entity is named C, then the full name is A.B.C
	 *
	 * @return the full name of this entity
	 */
	public final String getFullName() {
		if (parent==null) return getName();
		final StringBuilder sb=new StringBuilder(parent.getFullName());
		sb.append('.');
		sb.append(getName());
		if ((getAddress()==null)||(getAddress().length==0)) return sb.toString();
		sb.append(getAddress()[parent.getLevel()]);
		return sb.toString();
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
		return false; // if we get here, no port with a pending message has been found
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
	 * @return true if the port is inport or outport in this entity
	 */
	public final boolean hasPort(AbstractPort port) {
		return hasInport(port)||hasOutport(port);
	}

	/**
	 * Sets the address of this model. Should only be used internally when changing the model.
	 * <p>
	 * Note: The address of the root domain is {@code int[0]}. Another dimension has to be added per model level. The
	 * value of each dimension is the index within the corresponding level.
	 *
	 * @param addr address as branching code
	 */
	public final void setAddress(int[] addr) {
		address=addr;
		resetLevel();
	}

	/**
	 * Used internally for updating cached values if the structure changes (e.g. model is moved to another parent)
	 */
	public final void resetLevel() {
		level=INIT_LEVEL;
	}

	/**
	 * Resets the entity's address based on its position in the model structure.
	 * <p>
	 * Uses the parent's address and an additional index given by the caller. This method should be called if the
	 * structure changes (e.g. this entity is moved to another domain) It can also be use to initialize the address.
	 *
	 * @param index the new index value of this entity
	 */
	public void resetAddress(int index) {
		resetLevel();
		final int[] pAddr=getParent().getAddress();
		if ((address==null)||(address.length!=(pAddr.length+1))) address=new int[pAddr.length+1];
		int i=0;
		for (; i<pAddr.length; i++) address[i]=pAddr[i];
		address[i]=index; // i==pAddr.length
	}

	/**
	 * Removes an existing inport from the model.
	 *
	 * @param port the port
	 * @throws UnsupportedOperationException if the simulation is running
	 * @throws InvalidPortException          if the port is unknown
	 */
	public final void removeInport(AbstractPort port) throws UnsupportedOperationException, InvalidPortException {
		removePort(inports,port);
	}

	/**
	 * Removes an existing outport from the model.
	 *
	 * @param port the port
	 * @throws UnsupportedOperationException if the simulation is running
	 * @throws InvalidPortException          if the port is unknown
	 */
	public final void removeOutport(AbstractPort port) throws UnsupportedOperationException, InvalidPortException {
		removePort(outports,port);
	}

	private void removePort(List<AbstractPort> portList, AbstractPort port) {
		if (AbstractAgent.isSimulationRunning())
			throw new UnsupportedOperationException("Tried to remove a port during simulation cycle in "+getFullName());
		if (!portList.remove(port))
			throw new InvalidPortException("Tried to remove an unknown port from agent in "+getFullName());
	}

	/** private utility method to add port to port lists */
	private AbstractPort addPort(List<AbstractPort> portList, AbstractPort port) {
		if (AbstractAgent.isSimulationRunning())
			throw new UnsupportedOperationException("Tried to add a port during a simulation run in "+getFullName());
		if (portList.contains(port))
			throw new UniqueConstraintViolationException("A port may not be added twice to the same model!");
		portList.add(port);
		return port;
	}

	final void setInportList(List<AbstractPort> list) {
		inports=list;
	}

	final void setOutportList(List<AbstractPort> list) {
		outports=list;
	}

	/**
	 * Sets the parent for this model.
	 * <p>
	 * Note: Using this during a simulation run may crash the simulation!
	 *
	 * @param parent which should become the parent of this model
	 */
	final void setParent(AbstractDomain par) {
		parent=par;
		// any updating which is related to setting a new parent must be done in
		// the reset method - this method is overwritten in descendant classes
		resetLevel();
	}

	/**
	 * Tests if two model entities are equal (equality by identity)
	 *
	 * @param other entity to be compared with this one
	 * @return true if both entities are identical
	 */
	@Override
	public boolean equals(Object other) {
		return this==other;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFullName();
	}

}
