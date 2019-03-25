/*
 * JSimpleSim is a framework to build multi-agent systems in a quick and easy
 * way.
 *
 * This software is published as open source and licensed under the terms of GNU GPLv3.
 */
package org.simplesim.core.exceptions;

public class NotUniqueException extends RuntimeException {


	private static final long serialVersionUID=1L;

/**
   * Creates a new instance of NotUniqueException.
   */
  public NotUniqueException() {
    super();
  }

  /**
   * Creates a new instance of NotUniqueException.
   * 
   * @param message
   *          the message
   */
  public NotUniqueException(String message) {
    super(message);
  }

}
