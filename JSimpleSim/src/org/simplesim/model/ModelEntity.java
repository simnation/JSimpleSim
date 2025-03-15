/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import org.simplesim.core.messaging.Port;

/**
 * The {@code ModelEntity} is the core element of a simulation model. It
 * provides the most basic functionality needed by all entities of the model.
 * These are:
 * <ul>
 * <li>unit identification (by name or address)
 * <li>managing in- and outports
 * <li>utility methods (parent reference, naming, {@code toString},
 * {@code equals})
 * <li>providing relevant exceptions
 * </ul>
 */
public interface ModelEntity {

	/**
	 * Exception to be thrown if a duplicate object is used where only a unique one
	 * is allowed.
	 */
	@SuppressWarnings("serial")
	static final class UniqueConstraintViolationException extends RuntimeException {
		public UniqueConstraintViolationException(String message) {
			super(message);
		}
	}

	/**
	 * Exception to be thrown if a port cannot be added or removed.
	 */
	@SuppressWarnings("serial")
	static final class PortOperationException extends RuntimeException {
		public PortOperationException(String message) {
			super(message);
		}
	}

	/**
	 * Exception to be thrown if a port cannot be connected or disconnected.
	 */
	@SuppressWarnings("serial")
	static final class PortConnectionException extends RuntimeException {
		public PortConnectionException(String message) {
			super(message);
		}
	}

	int ROOT_LEVEL=0;
	
	/**
	 * Adds this entity to a domain.
	 * <p>
	 * The domain becomes the parent of this entity. 
	 * <p>
	 * <i>Note: Connection management has to be done externally by the caller!</i>
	 * 
	 */
//	void addToDomain(BasicDomain domain);

	/**
	 * Removes this entity form its parent domain.
	 * <p>
	 * This method should never be called during a simulation cycle. If the entity
	 * could be removed from the domain, the entity's parent is set to null.
	 * <p>
	 * <i>Note: Connection management has to be done externally by the caller.</i>
	 *
	 */
//	void removeFromDomain();

	/**
	 * Gets the entity address. Can be null.
	 * <p>
	 * Note: The address of the root domain is {@code int[0]}. Another dimension is
	 * added per model level. The value of each dimension is the index within the
	 * corresponding level.
	 *
	 * @return the address
	 */
	int[] getAddress();

	/**
	 * Returns the name of this model entity
	 * <p>
	 * Returns an empty string as default, may be overridden in derived classes.
	 *
	 * @return the name of this model entity, may be an empty string but not null
	 */
	String getName();

	/**
	 * Returns the full name of a model, concatenating the names of the parent
	 * entities.
	 * <p>
	 * Example: If A and B are parents of this entity and this entity is named C,
	 * then the full name is A.B.C
	 *
	 * @return the full name of this entity
	 */
	String getFullName();

	/**
	 * Returns the parent domain of this model entity.
	 *
	 * @return the parent of this model
	 */
	Domain getParent();

	/**
	 * Returns the level of the entity within the model hierarchy
	 * <p>
	 * Models may be organized in a hierarchy, so that each entity resides in a
	 * definite domain level of the model tree. The level information is generated
	 * when the getLevel() method is called first. The level of the root node is
	 * always {@value #ROOT_LEVEL}.
	 *
	 * @return the level of this entity in the model hierarchy
	 */
	int getLevel();

	Port getInport();

	Port getOutport();

}
