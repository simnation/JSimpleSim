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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.simplesim.model.ModelEntity;

/**
 * Ports are used to send messages within a model.
 * <p>
 * In the phase of model building, ports are connected with each others via the
 * {@code connectTo} method. The sending entity puts a {@code Message} on its
 * outport. The message is then routed along the established connections. A port
 * that has no further connection is always an end-point (destination of
 * delivery). Thus, connections are always <i>directed</i> from source outport
 * to destination inport.
 * <p>
 * Each port must also implement its own {@code copyMessages} strategy to
 * support message forwarding during the simulation run.
 *
 * @see SinglePort
 * @see MultiPort
 * @see SwitchPort
 * @see org.simplesim.model.RoutingDomain.RoutingPort RoutingPort
 */
public abstract class AbstractPort implements Port {

	/** parent model that contains this port */
	private final ModelEntity parent;

	/**
	 * List of messages that are communicated through this port. The initial size is
	 * set to 1. Thus memory consumption is low in the beginning, it will
	 * automatically be adapted if needed later on.
	 */
	private final List<AbstractMessage<?>> messages=new ArrayList<>(1);

	public AbstractPort(ModelEntity model) {
		parent=model;
	}

	@Override
	public final void clearMessages() {
		messages.clear();
	}

	@Override
	public final int countMessages() {
		return messages.size();
	}

	@Override
	public final boolean hasMessages() {
		return !messages.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <M extends AbstractMessage<?>> M poll() {
		if (!hasMessages()) return null;
		return (M) messages.remove(countMessages()-1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <M extends AbstractMessage<?>> Collection<M> readAll() {
		return (Collection<M>) messages;
	}

	@Override
	public final void write(AbstractMessage<?> message) {
		messages.add(message);
	}

	@Override
	public final void writeAll(Collection<AbstractMessage<?>> m) {
		messages.addAll(m);
	}

	@Override
	public final ModelEntity getParent() { return parent; }

	@Override
	public final boolean equals(Object obj) {
		return (this==obj);
	}

}
