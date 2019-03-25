/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.exceptions;


public class PortTypeMismatchException extends RuntimeException {

  static final long serialVersionUID = 1L;

  /**
   * Constructor.
   */
  public PortTypeMismatchException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message
   *          which is piped out if this Exception occurs
   */
  public PortTypeMismatchException(String message) {
    super(message);
  }

}
