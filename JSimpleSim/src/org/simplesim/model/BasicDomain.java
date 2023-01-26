/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implements all basic functionality of a domain.
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
public abstract class BasicDomain extends BasicModelEntity implements Domain {

	public static final int ROOT_ADDRESS[]=new int[0];

	/** set of child entities (agents or domains). */
	private final List<ModelEntity> entityList=new ArrayList<>(); // Only to be used internally!
	
	/** unmodifiable external view of the entityList*/
	private final List<ModelEntity> unmodifiableEntityList=Collections.unmodifiableList(entityList);
	
	
	@Override
	public final List<Agent> listAllAgents(boolean recursive) {
		final List<Agent> result=new ArrayList<>();
		for (final ModelEntity iter : listDomainEntities())
			if (iter instanceof Agent) result.add((Agent) iter);
			else if (recursive&&(iter instanceof Domain))
				result.addAll(((Domain) iter).listAllAgents(true));
		return result;
	}

	@Override
	public final List<ModelEntity> listDomainEntities() {
		return unmodifiableEntityList;
	}

	<T extends BasicModelEntity> T addEntity(T entity) {
		if (entity==null) throw new NullPointerException("Cannot add null pointer to domain "+getFullName());
		if (containsEntity(entity)) throw new UniqueConstraintViolationException(
				"Model "+entity.toString()+" added twice to domain "+this.getFullName());
		entity.setParent(this);
		getModifiableEntityList().add(entity);
		return entity;
	}

	<T extends BasicModelEntity> void removeEntity(T entity) {
		if (!getModifiableEntityList().remove(entity)) throw new NoSuchElementException("Entity not part of parent domain: "+entity.getFullName());
		entity.setParent(null);
		return;
	}

	/**
	 * The modifiable list should only be used internally to add and remove entities.
	 * For all other purposes use {@code listDomainEntities()}.
	 * 
	 */
	List<ModelEntity> getModifiableEntityList() {
		return entityList;
	}

}
