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

/**
 * Abstract base class for messages that are sent from one port to another.
 * <p>
 * Messages always contain a content and may be extended with additional features. If agents are
 * connected <i>directly</i>, the destination can be omitted and set to {@code null}. 
 * <p>
 * Note: This class is immutable and thus thread-safe.
 * 
 * @param <A> type of addressing
 * 
 * @see Message
 * @see RoutedMessage
 * 
 */
public class AbstractMessage<A> {
	
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
	public AbstractMessage(A s, A d, Object  c) {
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
	
	@SuppressWarnings("unchecked")
	public final <C> C getContent() {
		return (C) content;
	};
	

}
