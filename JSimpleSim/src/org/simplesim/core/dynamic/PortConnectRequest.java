/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.core.messaging.AbstractPort;

/**
 * Request to connect two ports.
 *
 */
public class PortConnectRequest implements ChangeRequest {
	
	private final AbstractPort fromPort, toPort;
	
	public PortConnectRequest(AbstractPort from, AbstractPort to) {
		fromPort=from;
		toPort=to;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.dynamic.ChangeRequest#doModelChange()
	 */
	public void doModelChange() {
		fromPort.connect(toPort);
	}

}
