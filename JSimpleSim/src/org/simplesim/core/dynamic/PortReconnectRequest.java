/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

import org.simplesim.core.messaging.AbstractPort;

/**
 * Request to disconnect two port.
 *
 */
public class PortReconnectRequest implements ChangeRequest {
	
	private final AbstractPort srcPort,oldDest, newDest;
	
	public PortReconnectRequest(AbstractPort port, AbstractPort oldTo, AbstractPort newTo) {
		srcPort=port;
		oldDest=oldTo;
		newDest=newTo;
	}

	/* (non-Javadoc)
	 * @see org.simplesim.core.dynamic.ChangeRequest#doModelChange()
	 */
	public void doModelChange() {
		srcPort.disconnect(oldDest);
		srcPort.connectTo(newDest);
	}

}
