/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.core.messaging.Port;

/**
 * Request to disconnect two port.
 *
 */
public class PortDisconnectRequest implements ChangeRequest {
	
	private final Port fromPort, toPort;
	
	public PortDisconnectRequest(Port from, Port to) {
		fromPort=from;
		toPort=to;
	}

	@Override
	public void doModelChange() {
		fromPort.disconnect(toPort);
	}

}
