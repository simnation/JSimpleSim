/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 *
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.model.BasicModelEntity;
import org.simplesim.model.Domain;

/**
 * Request to remove an entity from the model.
 * <p>
 * There must not be any enclosed sub-entities, if the entity to be removed is a
 * domain.
 *
 */
public class RemoveEntityRequest implements ChangeRequest {

	final BasicModelEntity entity;

	/**
	 * Set the entity to be moved and the new domain.
	 *
	 * @param what {@code AbtractAgent} to be moved
	 */
	public RemoveEntityRequest(BasicModelEntity what) {
		entity=what;
	}

	@Override
	public void doModelChange() {
		if ((entity instanceof Domain)&&!((Domain) entity).isEmpty()) throw new ChangeRequestException(
				"Only empty domains may be removed. Domain "+entity.getFullName()+" still has child entities!");
		entity.getParent().removeEntity(entity);
	}

}
