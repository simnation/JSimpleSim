/*
 * JSimpleSim is a framework to build multi-entity systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.model.BasicModelEntity;
import org.simplesim.model.BasicDomain;

/**
 * Request to add an entity to a domain.
 *
 */
public final class AddEntityRequest implements ChangeRequest {
	
	private final BasicDomain toDomain;
	private final BasicModelEntity entity;
		
	/**
	 * Sets the entity to be moved and the new domain.
	 * 
	 * @param what entity to be moved
	 * @param dest new domain of the entity
	 */
	public AddEntityRequest(BasicModelEntity what, BasicDomain dest) {
		entity=what;	
		toDomain=dest;
	}

	public void doModelChange() {
		toDomain.addEntity(entity);
	}

}
