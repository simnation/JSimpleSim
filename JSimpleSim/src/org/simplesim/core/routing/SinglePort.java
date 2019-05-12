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
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.simplesim.model.AbstractAgent;
import org.simplesim.model.AbstractDomain;
import org.simplesim.model.BasicModelEntity;

/**
 * Port connecting an outport with a single inport.
 * <p>
 * The parent entities of either port may be an {@link AbstractAgent} or an {@link AbstractDomain}
 * 
 * @see Message
 * @see MultiPort
 *  
 */
public final class SinglePort extends AbstractPort {

	/** Save single destination port in minimal ArrayList because this 
	 *  is often need in copyMessages().
	 */
	private final List<AbstractPort> destination=new ArrayList<>(1);

	public SinglePort(BasicModelEntity model) {
		super(model);
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.routing.AbstractPort#connectTo(org.simplesim.core.routing.AbstractPort)
	 */
	@Override
	public void connectTo(AbstractPort port) {
		if (!isEndPoint()) throw new PortConnectionException("Connection of SinglePort alread in use in "+getParent().getFullName());
		destination.add(port);
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.routing.AbstractPort#disconnect(org.simplesim.core.routing.AbstractPort)
	 */
	@Override
	public void disconnect(AbstractPort port) {
		if (!isConnectedTo(port)) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
		destination.clear();
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.routing.AbstractPort#isEndPoint()
	 */
	@Override
	public boolean isEndPoint() {
		return destination.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.routing.AbstractPort#isConnectedTo(org.simplesim.core.routing.AbstractPort)
	 */
	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return port.equals(getConnection());
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.routing.AbstractPort#copyMessages()
	 */
	@Override
	public Collection<AbstractPort> copyMessages() {
		if (!hasMessages()) return Collections.emptyList();
		getConnection().writeAll(this.readAll());
		clearMessages();
		return destination;
	}

	/**
	 * Returns the destination of this port.
	 * 
	 * @return destination port of this connection
	 */
	public AbstractPort getConnection() {
		return destination.get(0);
	}

}
