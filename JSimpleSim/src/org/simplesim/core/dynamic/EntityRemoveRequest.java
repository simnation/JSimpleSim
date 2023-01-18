/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.model.BasicDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * Request to remove an entity from the model.
 * <p>
 * There must not be any enclosed sub-entities, if the entity to be removed is a domain. 
 *
 */
public class EntityRemoveRequest implements ChangeRequest {
	
	final BasicModelEntity entity;
	
	/**
	 * Set the entity to be moved and the new domain.
	 * 
	 * @param who {@code AbtractAgent} to be moved
	 */
	public EntityRemoveRequest(BasicModelEntity who) {
		entity=who;	
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.dynamic.ChangeRequest#doModelChange()
	 */
	public void doModelChange() {
		if ((entity instanceof BasicDomain) && (((BasicDomain) entity).countDomainEntities()!=0))
			throw new ChangeRequestException("Only empty domains may be removed. Domain "+entity.getFullName()+" still has child entities!");
		if (entity.getParent().removeEntity(entity)==null) throw new ChangeRequestException(
				"Model entity "+entity.getFullName()+" could not removed from "+entity.getParent().getFullName());
	}

}
