/*
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
package org.simplesim.core.messaging;

import org.simplesim.model.Agent;

/**
 * Class for messages for direct (non-routed) message forwarding.
 * <p>
 * Messages always contain a content and may be extended with additional features. If agents are
 * connected <i>directly</i>, the destination can be omitted and set to {@code null}. 
 * <p>
 * Note: This class is read-only and thus thread-safe.	
 * 
 * @see SinglePort
 * @see MultiPort
 * @see SwitchPort
 * @see org.simplesim.model.DirectMessageForwarding
 * @see org.simplesim.model.RecursiveMessageForwarding
 * 
 */
public final class Message extends AbstractMessage<Agent> {
	
	public Message(Agent s, Agent d, Object  c) {
		super(s,d,c);
	}
	
	public Message(Agent s, Object c) {
		super(s,null,c);
	}

}
