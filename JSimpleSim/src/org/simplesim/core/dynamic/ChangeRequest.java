/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy way. This software is published as open
 * source and licensed under the terms of GNU GPLv3.
 * 
 * Contributors: - Rene Kuhlemann - development and initial implementation
 */
package org.simplesim.core.dynamic;

/**
 * 
 *
 */
public interface ChangeRequest {
	
	/**
	 * Exception to be thrown if a change request is unsuccessful
	 */
	@SuppressWarnings("serial")
	public static class ChangeRequestException extends RuntimeException {
		public ChangeRequestException(String message) {
			super(message);
		}
	}
	
	void doModelChange();

}
