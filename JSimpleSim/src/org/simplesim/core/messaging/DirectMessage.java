/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.messaging;

import org.simplesim.model.AbstractAgent;

/**
 * Message class for using {@link DirectMessageForwarding}.
 * <p>
 * This class is immutable and thus thread-safe.
 */
public final class DirectMessage extends Message<AbstractAgent<?, ?>> {

	public DirectMessage(AbstractAgent<?, ?> source, Object content) {
		super(source,null,content);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [content="+getContent().toString()+", src="+getSource().getFullName()+"]";
	}

}
