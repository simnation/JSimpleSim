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

/**
 * Part of the observer patter to be implemented by observers listening to the source.
 *
 */
public interface Listener<T> {

	/**
	 * Notifies all listeners of a specific event or hook
	 * 
	 * @param source the source object of the call
	 */
	public void notifyListener(T source);
	
}
