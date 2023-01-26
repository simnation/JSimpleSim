/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import java.util.List;

/**
 *
 *
 */
public interface Domain extends ModelEntity {

	/**
	 * Adds the given entity to this domain.
	 * <p>
	 * The entity should not be added to any another domain at the same time. Also,
	 * this method should never be called during a simulation cycle.
	 *
	 * @param entity the model to be added
	 * @throws UniqueConstraintViolationException if the entity is already part of
	 *                                            this domain
	 * @throws NullPointerException               if entity is null
	 * @return the given entity for further usage
	 *
	<T extends BasicModelEntity> T addEntity(T entity);*/

	/**
	 * Removes the given entity from this domain.
	 * <p>
	 * This method should never be called during a simulation cycle. If the entity
	 * could be removed from this domain, the entity's parent is set to null!
	 * <p>
	 * <i>Note: Connection management has to be done externally by the caller!</i>
	 *
	 * @param entity the model to be removed
	 * @return the removed entity if the domain contained it, null otherwise
	 *
	<T extends BasicModelEntity> T removeEntity(T entity);*/

	/**
	 * Returns all agents within this domain
	 *
	 * @param recursive true if listing should be done recursively for agents in all
	 *                  subdomains, too
	 * @return list of all agents of this domain
	 */
	List<Agent> listAllAgents(boolean recursive);

	/**
	 * Returns an unmodifiable list of all entities of this domain - an entity can
	 * either be an agent or another domain.
	 *
	 * @return an unmodifiable list of all entities of this domain
	 */
	List<ModelEntity> listDomainEntities();

	/**
	 * Checks, if this domain contains a given entity.
	 *
	 * @param model the model
	 * @return true, if this domain contains the entity
	 */
	default boolean containsEntity(ModelEntity entity) {
		return listDomainEntities().contains(entity);
	}


	/**
	 * Returns the number of entities of the domain.
	 *
	 * @return the sub model count
	 */
	default int countDomainEntities() {
		return listDomainEntities().size();
	}

	/**
	 * Returns the root domain of the model.
	 *
	 * @return the root domain
	 */
	default Domain getRoot() {
		if (isRoot()) return this;
		return getParent().getRoot();
	}

	/**
	 * Checks if this domain is the root of the model.
	 *
	 * @return true if this domain it the root of the model
	 */
	default boolean isRoot() {
		return getParent()==null;
	}


	default boolean isEmpty() {
		return listDomainEntities().isEmpty();
	}
	

}
