/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements all basic functionality of a domain.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities may be agents or other
 * domains. Therefore, simulation model are build as a tree-like structure with {@code AbstractDomain} as branching and
 * {@link AbstractAgent} as leaf, resembling a composite pattern. The domain adds the following features:
 * <ul>
 * <li>add and remove entities to this domain
 * <li>give an overview of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public abstract class AbstractDomain extends BasicModelEntity {

	public static final int ROOT_ADDRESS[]=new int[0];

	/** set of child entities (agents or domains). */
	private final List<BasicModelEntity> entityList=new ArrayList<>();

	/**
	 * Adds the given entity to this domain.
	 * <p>
	 * The entity should not be added to any another domain at the same time. Also, this method should never be called
	 * during a simulation cycle.
	 *
	 * @param entity the model to be added
	 * @throws NotUniqueException   if the entity is already part of this domain
	 * @throws NullPointerException if entity is null
	 */
	public void addEntity(BasicModelEntity entity) {
		if (entity==null) throw new NullPointerException("Cannot add null pointer to domain "+getFullName());
		if (containsEntity(entity)) throw new UniqueConstraintViolation(
				"Model "+entity.toString()+" added twice to domain "+this.getFullName());
		entity.setParent(this);
		getEntityList().add(entity);
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
	public BasicModelEntity removeEntity(BasicModelEntity entity) {
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
	public final List<AbstractAgent<?, ?>> listAllAgents(boolean recursive) {
		final List<AbstractAgent<?, ?>> result=new ArrayList<>();
		for (final BasicModelEntity iter : getEntityList())
			if (iter instanceof AbstractAgent) result.add((AbstractAgent<?, ?>) iter);
			else if (recursive&&(iter instanceof AbstractDomain))
				result.addAll(((AbstractDomain) iter).listAllAgents(true));
		return result;
	}

	/**
	 * Returns an {@link Iterable} over the set of entities of this domain, an entity can either be an agent or another
	 * domain.
	 *
	 * @return an iterator for iterating over all entities of this domain
	 */
	public final Iterable<BasicModelEntity> listDomainEntities() {
		return getEntityList();
	}

	/**
	 * Checks for model.
	 *
	 * @param model the model
	 * @return true, if checks for model
	 */
	public final boolean containsEntity(BasicModelEntity model) {
		return getEntityList().contains(model);
	}

	public final BasicModelEntity getDomainEntity(int index) {
		return getEntityList().get(index);
	}

	List<BasicModelEntity> getEntityList() {
		return entityList;
	}

}
