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
import static org.simplesim.model.BasicModelEntity.UniqueConstraintViolation;


/**
 * Port connecting an outport with several inports.
 * <p>
 * Messages from the outport are copied to all connected inports. The parent entities 
 * of any port may be an {@link AbstractAgent} or an {@link AbstractDomain}
 *
 * @see Message
 * @see SinglePort
 */
public final class MultiPort extends AbstractPort {

	private final List<AbstractPort> destinations=new ArrayList<>();

	public MultiPort(BasicModelEntity model) {
		super(model);
	}

	@Override
	public void connectTo(AbstractPort port) {
		if (destinations.contains(port)) throw new UniqueConstraintViolation("MultiPort in "+this.getParent().getFullName()
				+" may not be connected twice to "+port.getParent().getFullName());
		destinations.add(port);
	}

	@Override
	public void disconnect(AbstractPort port) {
		if (!destinations.remove(port)) throw new PortConnectionException(
				"Cannot disconnect from a port that has never been connected in "+getParent().getFullName());
	}

	@Override
	public boolean isEndPoint() {
		return destinations.isEmpty();
	}

	@Override
	public Collection<AbstractPort> forwardMessages() {
		if (!hasMessages()) return Collections.emptyList();
		for (final AbstractPort dest : destinations) dest.writeAll(this.readAll());
		clearMessages();
		return destinations;
	}

	@Override
	public boolean isConnectedTo(AbstractPort port) {
		return destinations.contains(port);
	}

}
