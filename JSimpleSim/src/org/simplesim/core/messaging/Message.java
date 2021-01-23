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
package org.simplesim.core.messaging;

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
	
	/**
	 * Generals constructor for all types of messages.
	 * <p>
	 * Note: This class is read-only and thus thread-safe 
	 * 
	 * @param s source of message
	 * @param d destination of message
	 * @param c the content
	 * 
	 */
	public Message(A s, A d, Object  c) {
		this.src=s;
		this.dest=d;
		this.content=c;
	}
	
	/**
	 * Constructor for direct messages.
	 * <p>
	 * To be used if model entities are connected directly, so the destination information is already given by the 
	 * connection itself.
	 * 
	 * @param s source of message
	 * @param c the content
	 * 
	 */
	public Message(A s, Object c) {
		this(s,null,c);
	}
	
	public final A getSource() {
		return(src);
	}

	public final A getDestination() {
		return(dest);
	}
	
	@SuppressWarnings("unchecked")
	public final <C> C getContent() {
		return (C) content;
	};

}
