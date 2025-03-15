/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import org.simplesim.core.messaging.Port;
import org.simplesim.core.messaging.SinglePort;

/**
 * Provides basic functionality needed by all entities within the simulation
 * model.
 * <p>
 * In more detail, that is:
 * <ul>
 * <li>unit identification (by name or address)
 * <li>managing in- and outports
 * <li>utility methods (model level, naming, {@code toString}, {@code equals})
 * <li>providing relevant exceptions
 * </ul>
 */
public abstract class BasicModelEntity implements ModelEntity {

	private static final int INIT_LEVEL=Integer.MIN_VALUE;

	/** Parent entity in model hierarchy. */
	private Domain parent=null;

	/** The inport (is always a SinglePort) */
	private Port inport=new SinglePort(this);

	/** The outport */
	private Port outport=null;

	/** Address in numbers, describing the model's branch within the model tree */
	private int[] address=null;

	/** The level in the hierarchy the model is located at. */
	private int level=INIT_LEVEL;
	
	
/*	@Override
	public void addToDomain(BasicDomain domain) {
		domain.addEntity(this);
	}

	@Override 
	public void removeFromDomain() {
		((BasicDomain) getParent()).removeEntity(this);
	}
*/
	@Override
	public String getName() { return ""; }

	@Override
	public String getFullName() {
		if (parent==null) return getName();
		return parent.getFullName()+'.'+getName();
	}

	@Override
	public Domain getParent() { return parent; }

	@Override
	public int[] getAddress() { return address; }

	@Override
	public Port getInport() { return inport; }

	@Override
	public Port getOutport() { return outport; }

	@Override
	public int getLevel() {
		// if there is no level information yet, re-compute it
		if (level==INIT_LEVEL) {
			if (parent==null) level=ModelEntity.ROOT_LEVEL;
			else level=parent.getLevel()+1;
		}
		return level;
	}

	protected Port setInport(Port port) { return (inport=port); }

	protected Port setOutport(Port port) { return (outport=port); }

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
	void setAddress(int[] addr) {
		address=addr;
		resetLevel();
	}

	/**
	 * Resets the entity's address based on its position in the model structure.
	 * <p>
	 * Uses the parent's address and an additional index given by the caller. This
	 * method should be called if the structure changes (e.g. this entity is moved
	 * to another domain). It can also be use to initialize the address.
	 *
	 * @param index the new index value of this entity within its domain
	 */
	void resetAddress(int index) {
		resetLevel();
		final int[] pAddr=getParent().getAddress();
		if ((address==null)||(address.length!=(pAddr.length+1))) address=new int[pAddr.length+1];
		for (int i=0; i<pAddr.length; i++) address[i]=pAddr[i];
		address[pAddr.length]=index;
	}

	/**
	 * Sets the parent for this model.
	 * <p>
	 * Note: Using this during a simulation run may crash the simulation!
	 *
	 * @param parent which should become the parent of this model
	 */
	void setParent(Domain par) {
		parent=par;
		// any updating which is related to setting a new parent must be done in
		// the reset method - this method is overwritten in descendant classes
		resetLevel();
	}

	/**
	 * Used internally for updating cached values if the structure changes (e.g.
	 * model is moved to another parent)
	 */
	private void resetLevel() { level=INIT_LEVEL; }

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

	@Override
	public String toString() {
		return getFullName();
	}

}
