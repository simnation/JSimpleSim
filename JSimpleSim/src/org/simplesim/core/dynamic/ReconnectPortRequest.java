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
public final class ReconnectPortRequest implements ChangeRequest {

	private final Port srcPort, oldDest, newDest;

	public ReconnectPortRequest(Port port, Port oldTo, Port newTo) {
		srcPort=port;
		oldDest=oldTo;
		newDest=newTo;
	}

	@Override
	public void doModelChange() {
		srcPort.disconnect(oldDest);
		srcPort.connect(newDest);
	}

}
