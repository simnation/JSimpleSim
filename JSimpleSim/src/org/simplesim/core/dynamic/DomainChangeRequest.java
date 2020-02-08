/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;

/**
 * Request to move an agent from one domain to an other.
 *
 */
public final class DomainChangeRequest implements ChangeRequest {
	
	private final AbstractAgent<?,?> agent;
	private final AbstractDomain fromDomain, toDomain;
	
	public DomainChangeRequest(AbstractAgent<?,?> who, AbstractDomain from, AbstractDomain to) {
		agent=who;
		fromDomain=from;
		toDomain=to;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.dynamic.ChangeRequest#doModelChange()
	 */
	public void doModelChange() {
		if (fromDomain.removeEntity(agent)==null) throw new ChangeRequestException("Agent "+agent.getFullName()+" could not removed from "+fromDomain.getFullName());
		toDomain.addEntity(agent);
	}

}
