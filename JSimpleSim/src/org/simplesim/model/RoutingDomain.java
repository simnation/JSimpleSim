/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.simplesim.core.routing.AbstractPort;
import org.simplesim.core.routing.RoutedMessage;
import org.simplesim.core.routing.SinglePort;

/**
 * Implements a domain suited for automatic message routing by using the message's address tag.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities may be agents or other
 * domains. Therefore, simulation model are build as a tree-like structure with {@code AbstractDomain} as branching and
 * {@link AbstractAgent} as leaf, resembling a composite pattern. The domain adds the following features:
 * <ul>
 * <li>offer message routing by adding a {@code RoutingPort}
 * <li>give an overview of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public abstract class RoutingDomain extends AbstractDomain {

	/**
	 * Port for automatic message routing.
	 * <p>
	 * Routing is done by reading the messages' destination descriptions and sending the message along the right
	 * connection accordingly. Thus only a {@link RoutedMessage} can be handled by this port since it contains
	 * additional address information.
	 * <p>
	 * The operation modus is similar to a {@link MulitPort}, but the messages is only forward to ONE port of the
	 * destination list, <i>not</i> all.
	 * <p>
	 * Note: This implementation references directly to the {@code entityList} of its parent domain and has no list of
	 * connected ports of it own. This facilitates handling of model changes.
	 */
	private class RoutingPort extends AbstractPort {

		public RoutingPort(BasicModelEntity model) {
			super(model);
		}

		@Override
		public void connectTo(AbstractPort port) {
			throw new UnsupportedOperationException("Connection is done automatically when adding an entity");
		}

		@Override
		public void disconnect(AbstractPort port) {
			throw new UnsupportedOperationException("Disconnection is done automatically when removing an entity");
		}

		@Override
		public boolean isEndPoint() {
			return getEntityList().isEmpty();
		}

		@Override
		protected Collection<AbstractPort> copyMessages() {
			final Set<AbstractPort> result=new HashSet<>(); // set to ensure no duplicates in destination list
			while (hasMessages()) {
				final RoutedMessage msg=(RoutedMessage) poll(); // message is also removed in this step!
				final int index=msg.getDestIndex(getLevel()); // index corresponding to level in model
				final BasicModelEntity entity=getEntityList().get(index); // find the right entity for forwarding
				final AbstractPort dest=entity.getInport(0); // find the right port for forwarding
				dest.write(msg);
				result.add(dest);
			}
			return result;
		}

		@Override
		public boolean isConnectedTo(AbstractPort port) {
			return containsEntity(port.getParent());
		}

	}

	public RoutingDomain() {
		setInportList(Collections.singletonList(new RoutingPort(this)));
		setOutportList(Collections.singletonList(new SinglePort(this)));
	}

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
	@Override
	public final void addEntity(BasicModelEntity entity) {
		super.addEntity(entity);
		entity.resetAddress(countDomainEntities()-1); // reset addresses in entity and its children
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
	@Override
	public final BasicModelEntity removeEntity(BasicModelEntity entity) {
		final int start=getEntityList().indexOf(entity);
		if (start==-1) return null; // unknown entity
		getEntityList().remove(start);
		entity.setParent(null);
		for (int index=start; index<countDomainEntities(); index++) {
			getEntityList().get(index).resetAddress(index);
		}
		return entity;
	}

	/**
	 * Used internally for updating the entity's address
	 * <p>
	 * This method can be use to initialize the address. It should be called always if the structure changes (e.g. this
	 * entity is moved to another domain)
	 *
	 * @param pAddr address array of the parent entity
	 * @param value the new index value of this entity
	 */
	@Override
	public void resetAddress(int value) {
		super.resetAddress(value); // update address of this domain
		// recursively update addresses of all child entities
		for (int index=0; index<countDomainEntities(); index++) {
			getEntityList().get(index).resetAddress(index);
		}
	}

}
