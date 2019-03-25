/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import org.simplesim.core.routing.Message;

/**
 *
 * This is the basic message class for all kinds of messages sent between agents.
 * 
 * This is like a normal letter: source and destination address plus content. 
 * 
 * Message forwarding policy depends on the type of ports used.
 * 
 * This class is immutable.
 *
 * @author Rene Kuhlemann
 *
 */

public final class RoutedMessage extends Message<int[]> {
			
	public RoutedMessage(int[] source, int[] destination, Object  content) {
		super(source,destination,content);
	}
		
	public int getDestIndex(int level) {
		return(getDestination()[level]);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [content=" + getContent().toString() + ", dest=" + getDestination() + ", src="
				+ getSource() + "]";
	}
	
}
