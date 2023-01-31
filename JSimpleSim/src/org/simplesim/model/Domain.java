/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.model;

import java.util.List;

/**
 * Domain are submodels of the simulation model.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities may be agents or other
 * domains. Therefore, simulation model are build as a tree-like structure with {@code Domain} as branching and
 * {@link BasicAgent} as leaf, resembling a composite pattern. The domain adds the following features for entity management:
 * <ul>
 * <li>add and remove entities to this domain
 * <li>provide information of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public interface Domain extends ModelEntity {

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
	 * @param entity the entity to be looked up
	 * 
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
