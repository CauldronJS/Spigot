package me.conji.cauldron;

import org.graalvm.polyglot.Value;

public class Console {
  Cauldron cauldron;

  public Console(Cauldron cauldron) {
    this.cauldron = cauldron;
  }

  public void log(Value... contents) {

  }

  public void error(Value... contents) {

  }

  public void debug(Value... contents) {

  }

  public void warn(Value... contents) {

  }
}