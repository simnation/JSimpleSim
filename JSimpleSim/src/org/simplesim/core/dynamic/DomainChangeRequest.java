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
 * Request to move an entity from one domain to an other.
 * <p>
 * The originating domain is always the entity's parent domain.
 *
 */
public class DomainChangeRequest extends EntityRemoveRequest {
	
	private final BasicDomain toDomain;
	
	/**
	 * Sets the entity to be moved and the new domain.
	 * 
	 * @param what entity to be moved
	 * @param dest new domain of the entity
	 */
	public DomainChangeRequest(BasicModelEntity what, BasicDomain dest) {
		super(what);	
		toDomain=dest;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.dynamic.ChangeRequest#doModelChange()
	 */
	public void doModelChange() {
		super.doModelChange(); // remove the entity
		entity.addToDomain(toDomain);
	}

}
