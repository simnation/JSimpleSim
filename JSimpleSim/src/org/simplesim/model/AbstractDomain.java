/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements all basic functionality of a domain.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities may be agents or other
 * domains. Therefore, simulation model are build as a tree-like structure with {@code AbstractDomain} as branching and
 * {@link AbstractAgent} as leaf, resembling a composite pattern. The domain adds the following features for entity management:
 * <ul>
 * <li>add and remove entities to this domain
 * <li>provide information of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public abstract class AbstractDomain extends BasicModelEntity {

	public static final int ROOT_ADDRESS[]=new int[0];

	/** set of child entities (agents or domains). */
	private final List<BasicModelEntity> entityList=new ArrayList<>();
	
	/** unmodifiable external view of the entityList*/
	private final List<BasicModelEntity> unmodifiableEntityList=Collections.unmodifiableList(entityList);
	
	
	/**
	 * Adds the given entity to this domain.
	 * <p>
	 * The entity should not be added to any another domain at the same time. Also, this method should never be called
	 * during a simulation cycle.
	 *
	 * @param entity the model to be added
	 * @throws UniqueConstraintViolationException   if the entity is already part of this domain
	 * @throws NullPointerException if entity is null
	 * @return the given entity for further usage
	 */
	public <T extends BasicModelEntity> T addEntity(T entity) {
		if (entity==null) throw new NullPointerException("Cannot add null pointer to domain "+getFullName());
		if (containsEntity(entity)) throw new UniqueConstraintViolationException(
				"Model "+entity.toString()+" added twice to domain "+this.getFullName());
		entity.setParent(this);
		getEntityList().add(entity);
		return entity;
	}

	/**
	 * Removes the given entity from this domain.
	 * <p>
	 * This method should never be called during a simulation cycle. If the entity could be removed from this domain,
	 * the entity's parent is set to null!
	 *
	 * @param entity the model to be removed
	 * @return the removed entity if the domain contained it, null otherwise
	 */
	public <T extends BasicModelEntity> T removeEntity(T entity) {
		if (!getEntityList().remove(entity)) return null;
		entity.setParent(null);
		return entity;
	}

	/**
	 * Returns the number of entities of this domain.
	 *
	 * @return the sub model count
	 */
	public final int countDomainEntities() {
		return getEntityList().size();
	}

	/**
	 * Returns all agents within this domain
	 *
	 * @param recursive true if listing should be done recursively for agents in all subdomains, too
	 * @return list of all agents of this domain
	 */
	public final List<Agent> listAllAgents(boolean recursive) {
		final List<Agent> result=new ArrayList<>();
		for (final BasicModelEntity iter : getEntityList())
			if (iter instanceof Agent) result.add((Agent) iter);
			else if (recursive&&(iter instanceof AbstractDomain))
				result.addAll(((AbstractDomain) iter).listAllAgents(true));
		return result;
	}

	/**
	 * Returns an unmodifiable list of all entities of this domain - an entity can either be an agent or another
	 * domain.
	 *
	 * @return an unmodifiable list of all entities of this domain 
	 */
	public final List<BasicModelEntity> listDomainEntities() {
		return unmodifiableEntityList;
	}

	/**
	 * Checks, if this domain contains a given model.
	 *
	 * @param model the model
	 * @return true, if this domain contains the given submodel
	 */
	public final boolean containsEntity(BasicModelEntity model) {
		return getEntityList().contains(model);
	}
	
	/**
	 * Checks, if this domain is the root of the model.
	 *
	 * @return true, if this domain it the root of the model
	 */
	public final boolean isRoot() {
		return getParent()==null;
	}

	/**
	 * Returns the root domain of the model.
	 *
	 * @return the root domain
	 */
	public final AbstractDomain getRoot() {
		if (isRoot()) return this;
		return getParent().getRoot();
	}
	
	public final boolean isEmpty() {
		return getEntityList().isEmpty();
	}
	
	
	/**
	 * Direct access to {@code entityList} - should only be used internally.
	 *
	 * @return the modifiable list of entities of this domain
	 */
	List<BasicModelEntity> getEntityList() {
		return entityList;
	}

}
