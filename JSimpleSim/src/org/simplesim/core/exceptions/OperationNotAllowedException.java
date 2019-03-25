/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.exceptions;


public class OperationNotAllowedException extends RuntimeException {

	private static final long serialVersionUID=1L;

/**
   * Creates a new instance of OperationNotAllowedException.
   */
  public OperationNotAllowedException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message
   *          which describes the error message
   */
  public OperationNotAllowedException(String message) {
    super(message);
  }

}
