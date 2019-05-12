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

/**
 * Base class for messages that are sent from one port to another.
 * <p>
 * Messages always contain a content and may be extended with additional features.
 * 
 * @param <A> type of addressing
 * 
 */
public class Message<A> {
	
	private final A src, dest;
	private final Object content;
	
	
	public Message(A s, A d, Object  c) {
		this.src=s;
		this.dest=d;
		this.content=c;
	}
	
	public final A getSource() {
		return(src);
	}

	public final A getDestination() {
		return(dest);
	}
	
	public final Object getContent() {
		return content;
	};

}
