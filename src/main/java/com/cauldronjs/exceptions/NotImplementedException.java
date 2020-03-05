package com.cauldronjs.exceptions;

public class NotImplementedException extends Exception {
  private static final long serialVersionUID = 8376346047850458885L;

  public NotImplementedException(String name) {
    super("Not implemented: " + name);
  }
}