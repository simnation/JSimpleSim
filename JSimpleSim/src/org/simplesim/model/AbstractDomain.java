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
import java.util.Collection;
import java.util.List;

import org.simplesim.core.routing.RoutingPort;

/**
 * Implements all basic functionality of a domain.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities
 * may be agents or other domains. Therefore, simulation model are build as a tree-like structure with
 * {@code AbstractDomain} as branching and {@link AbstractAgent} as leaf, resembling a composite pattern.
 * The domain adds the following features:
 * <ul>
 * <li>a {@link IBulletinBord} to provide further information for the agents of the domain
 * <li>offer message routing by adding a {@code RoutingPort}
 * <li>give an overview of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public abstract class AbstractDomain<B extends IBulletinBoard> extends BasicModelEntity {

	/** set of child entities (sub models). */
	private final List<BasicModelEntity> entities=new ArrayList<>();

	/** memory for domain specific information (statistics, external parameters) */
	private B bulletinBoard=null;

	/**
	 * Creates a new instance of a domain . This instance is named by the given
	 * parameter name.
	 *
	 * @param name the name
	 */
	public AbstractDomain(String name, int[] addr) {
		super(name,addr);
	}

	public AbstractDomain(String name) {
		this(name,null);
	}

	public AbstractDomain(int[] addr) {
		super(null,addr);
	}

	public final void setBulletinBoard(B board) {
		bulletinBoard=board;
	}

	/**
	 * Adds the given model to the list of submodels of this coupled model. This
	 * model should not be added to another coupled model at the same time - however
	 * this is not checked!
	 *
	 * @param entity the model
	 *
	 * @throws NotUniqueException if the entity is already part of this domain
	 */
	public final void addEntity(BasicModelEntity entity) {
		if (containsEntity(entity))
			throw new UniqueConstraintViolation("Model "+entity.getName()+" added twice to domain "+this.getFullName());
		entity.setParent(this);
		entities.add(entity);
	}

	/**
	 * Returns the number of sub models.
	 *
	 * @return the sub model count
	 */
	public final int countDomainEntities() {
		return entities.size();
	}

	/**
	 * Creates and adds a new {@link RoutingPort} as inport to the model. This
	 * enables auto-routing of {@link RoutedMessages} when passed down the model
	 * tree
	 *
	 *
	 * @return returns a reference to the new port for further usage
	 */
	public final RoutingPort addRoutingPort() {
		return (RoutingPort) addInport(new RoutingPort(this));
	}

	/**
	 * Returns all atomic submodels (i.e. list of agents) within this
	 * {@link AbstractDomain}.
	 *
	 * @return list of all agents / atomic submodels of this coupled model
	 */
	public final Collection<AbstractAgent<?, ?>> listAllAgents() {
		final Collection<AbstractAgent<?, ?>> result=new ArrayList<>();
		for (final BasicModelEntity iter : entities)
			if (iter instanceof AbstractAgent) result.add((AbstractAgent<?, ?>) iter);
			else if (iter instanceof AbstractDomain) result.addAll(((AbstractDomain<?>) iter).listAllAgents());
		return result;
	}

	/**
	 * Returns an {@link Iterable} over the set of sub models of this domain, a
	 * submodel can either be an Agent or another Domain.
	 *
	 * @return an iterator for iterating over all sub models
	 */
	public final Iterable<BasicModelEntity> listDomainEntities() {
		return entities;
	}

	/**
	 * Checks for model.
	 *
	 * @param model the model
	 *
	 * @return true, if checks for model
	 */
	public final boolean containsEntity(BasicModelEntity model) {
		return entities.contains(model);
	}

	/**
	 * @return the InfoBoard
	 */
	public final B getBulletinBoard() {
		return bulletinBoard;
	}

}
