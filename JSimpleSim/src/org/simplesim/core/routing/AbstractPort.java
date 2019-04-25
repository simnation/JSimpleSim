/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simplesim.model.BasicModelEntity;

/**
 * The {@code AbstractPort} is the base class of any port. Ports are used to
 * send messages within the model. In the phase of model building, ports can be
 * connected with each others via the {@code connectTo} method. Each port must
 * also implement its own {@code copyMessages} strategy to support message
 * forwarding during simulation. A port that has no further connection is always
 * an end-point (destination of delivery)
 *
 * @author Rene Kuhlemann
 */
public abstract class AbstractPort {

	/**
	 * The parent model that contains this port
	 */
	private final BasicModelEntity parent;

	/**
	 * List of objects which are communicated through this port. The initial size is
	 * set to 1. Thus memory consumption is low in the beginning, it will
	 * automatically be adapted if needed later on.
	 */
	private final List<Message<?>> values=new ArrayList<>(1);

	@SuppressWarnings("serial")
	static class PortConnectionException extends RuntimeException {

		public PortConnectionException(String message) {
			super(message);
		}

	}

	public AbstractPort(BasicModelEntity model) {
		parent=model;
	}

	public abstract void connectTo(AbstractPort target);

	public abstract void disconnect(AbstractPort target);

	public abstract boolean isEndPoint();

	public abstract boolean isConnectedTo(AbstractPort port);

	/**
	 * Implements a message forwarding strategy specific for the port class. Port must be connected to other ports,
	 * please check for end-point beforehand!
	 *
	 * @return list of destination ports that need further forwarding
	 */
	public abstract Collection<AbstractPort> copyMessages();

	public final void clear() {
		values.clear();
	}

	public final int countValues() {
		return values.size();
	}

	public final boolean hasValue() {
		return !values.isEmpty();
	}

	/**
	 * Returns and REMOVES the last element in message list or null if list is empty
	 *
	 * @return last element in message list or null if list is empty
	 */
	public final Message<?> read() {
		if (!hasValue()) return null;
		return values.remove(countValues()-1);
	}

	/**
	 * Returns the complete message list. Elements must be removed manually (e.g. by
	 * the {@code clear} method)
	 *
	 * @return the message list
	 */
	public final Collection<Message<?>> readAll() {
		return values;
	}

	public final void write(Message<?> m) {
		values.add(m);
	}

	public final void writeAll(Collection<Message<?>> m) {
		values.addAll(m);
	}

	@Override
	public final boolean equals(Object obj) {
		return (this==obj);
	}

	public final BasicModelEntity getParent() {
		return parent;
	}

}
