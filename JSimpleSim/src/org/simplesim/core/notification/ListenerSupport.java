/*
 * JSimpleSim is a framework to build mutli-agent systems in a quick and easy way.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.core.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the observer pattern by means of an auxiliary class and pull-mode notification
 * <p>
 * The {@code ListenerSupport} supplies all necessary administration functionality of the observer pattern.
 * That is registration, unregistration and notification of observers. All observers have to implement the
 * {@link Listener} interface using the observed class as type parameter.
 * <p>
 * Listening is done by pull-mode notification: after being called, the listener has to use the getter methods
 * of the source object to query for the necessary information. The notification itself is coupled to the
 * specific hook in the source class.
 * 
 * @param T type of the observed class
 *
 */
public class ListenerSupport<T> {
	
	private final List<Listener<T>> listeners=new ArrayList<>(4);
	
	public synchronized void registerListener(Listener<T> listener) {
		listeners.add(listener);
	}
	
	public synchronized void unregisterListener(Listener<T> listener) {
		listeners.remove(listener);
	}
	
	public void notifyListeners(T source) {
		for (Listener<T> iter : listeners) iter.notifyListener(source);
	}
	
}
