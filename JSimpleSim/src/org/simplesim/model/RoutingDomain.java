/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;

import org.simplesim.core.messaging.AbstractPort;
import org.simplesim.core.messaging.Port;
import org.simplesim.core.messaging.RoutingMessage;
import org.simplesim.core.messaging.SinglePort;

/**
 * Implements a domain suited for automatic message routing by using the message's address tag.
 * <p>
 * Domains serve as a compartment for other entities within the simulation model. These entities may be agents or other
 * domains. Therefore, simulation model are build as a tree-like structure with {@code Domain} as branching and
 * {@link Agent} as leaf, resembling a composite pattern. The domain adds the following features:
 * <ul>
 * <li>offer message routing by adding a {@code RoutingPort}
 * <li>give an overview of the entities contained in this domain
 * <li>list all agents in this domain and its subdomains
 * </ul>
 *
 * @see RoutingAgent
 * @see org.simplesim.core.messaging.RoutingMessage RoutedMessage
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Reference for composite pattern</a>
 */
public abstract class RoutingDomain extends BasicDomain {

	/**
	 * Port for automatic message routing.
	 * <p>
	 * Routing is done by reading the messages' destination descriptions and sending the message along the right
	 * connection accordingly. Thus only a {@link RoutingMessage} can be handled by this port since it contains
	 * additional address information.
	 * <p>
	 * The operation modus is similar to a {@link org.simplesim.core.messaging.MultiPort MultiPort}, but the messages is
	 * only forward to <i>one</i> port of the destination list, <i>not</i> all.
	 * <p>
	 * Note 1: This implementation references directly to the {@code entityList} of its parent domain and has no list of
	 * connected ports of it own. This facilitates handling of model changes.
	 * <p>
	 * Note 2: This implementation should only be used for forwarding down the model hierarchy. For forwarding up use a
	 * {@link SinglePort}.
	 */
	protected final class RoutingPort extends AbstractPort {

		public RoutingPort(ModelEntity parent) {
			super(parent);
		}

		@Override
		public void connect(Port port) {
			throw new PortConnectionException("Connection is done automatically when adding an entity");
		}

		@Override
		public void disconnect(Port port) {
			throw new PortConnectionException("Disconnection is done automatically when removing an entity");
		}

		@Override
		public boolean isEndPoint() {
			return isEmpty();
		}

		@Override
		public Collection<Port> forwardMessages() {
			final Collection<Port> result=new HashSet<>(); // set to ensure no duplicates in destination list
			while (hasMessages()) {
				final RoutingMessage msg=poll(); // message is also removed in this step!
				final int index=msg.getDestIndex(getLevel()); // destination index corresponding to entity level in model
				final ModelEntity entity=listDomainEntities().get(index); // find the right entity for forwarding
				final Port dest=entity.getInport(); // find the right port for forwarding
				dest.write(msg);
				result.add(dest);
			}
			return result;
		}

		@Override
		public boolean isConnectedTo(Port port) {
			return containsEntity(port.getParent());
		}

	}

	public RoutingDomain() {
		setInport(new RoutingPort(this));
		setOutport(new SinglePort(this));
	}

	/**
	 * Defines the domain as root domain of the model.
	 * <p>
	 * Should be called from the constructor of the derived class or during model building. Only call once!
	 */
	public void setAsRootDomain() {
		setParent(null);
		setAddress(ROOT_ADDRESS);
		getOutport().connect(getInport()); // close the loop at the root domain 
	}

	/**
	 * Adds the given entity to this domain.
	 * <p>
	 * The entity should not be added to any another domain at the same time. Also, this method should never be called
	 * during a simulation cycle.
	 *
	 * @param entity the model to be added
	 * @return the given entity for further usage
	 * @throws UniqueConstraintViolationException if the entity is already part of this domain
	 * @throws NullPointerException               if entity is null
	 */
	@Override
	<T extends BasicModelEntity> T addEntity(T entity) {
		super.addEntity(entity);
		entity.getOutport().connect(getOutport()); // upstream coupling through the domain towards the root
		// Note: The downstream coupling is handled by the RoutingPort itself!
		entity.resetAddress(countDomainEntities()-1); // reset addresses of the entity and its children
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
	@Override
	<T extends BasicModelEntity> void removeEntity(T entity) {
		final int start=listDomainEntities().indexOf(entity);
		if (start==-1) throw new NoSuchElementException("Entity not part of parent domain: "+entity.getFullName());
		entity.getOutport().disconnect(getOutport()); // remove connection towards domain root
		entity.setParent(null);
		getModifiableEntityList().remove(start);
		for (int index=start; index<countDomainEntities(); index++) {
			final BasicModelEntity bme=(BasicModelEntity) listDomainEntities().get(index);
			bme.resetAddress(index);
		}
	}

	/**
	 * Updates this entity's address after model changes
	 * <p>
	 * This method can be use to initialize the address. It should be called always if the structure changes (e.g. this
	 * entity is moved to another domain)
	 *
	 * @param value the new index value of this entity
	 */
	@Override
	protected final void resetAddress(int value) {
		super.resetAddress(value); // update address of this domain
		// recursively update addresses of all child entities
		for (int index=0; index<countDomainEntities(); index++) {
			final BasicModelEntity bme=(BasicModelEntity) listDomainEntities().get(index);
			bme.resetAddress(index);
		}
	}

}
