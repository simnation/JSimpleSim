/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3. Contributors: - Rene Kuhlemann - development and initial
 * implementation
 */
package org.simplesim.core.messaging;

/**
 * Special message class for using auto routing.
 * <p>
 * If using a {@link org.simplesim.model.RoutingDomain.RoutingPort RoutingPort} to use the routing functionality,
 * messages have to be derived from this class.
 * <p>
 * The addresses describe the model's branch within the model tree as int arrays. The level of the tree is the index of
 * the array whereas the actual number is the id of the agent within the model level. The level of the root domain is
 * always 0.
 * <p>
 * This class is immutable and thus thread-safe.
 */
public final class RoutedMessage extends AbstractMessage<int[]> {

	public RoutedMessage(int[] source, int[] destination, Object content) {
		super(source,destination,content);
	}

	public int getDestIndex(int level) {
		return getDestination()[level];
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [content="+getContent().toString()+", dest="+getDestination()+", src="+getSource()+"]";
	}

}
