/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import org.simplesim.core.routing.Message;
import org.simplesim.model.AbstractAgent;

/**
 *
 * This is the message class for using the {@link DirectMessageForwarding}. 
 * 
 * This class is immutable.
 *
 * @author Rene Kuhlemann
 *
 */

public final class DirectMessage extends Message<AbstractAgent<?,?>> {
		
	public DirectMessage(AbstractAgent<?,?> source, Object  content) {
		super(source,null,content);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [content=" + getContent().toString()+ ", src="
				+ getSource().getFullName() + "]";
	}
	
}
