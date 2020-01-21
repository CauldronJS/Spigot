package me.conji.cauldron.internal.modules;

import org.graalvm.polyglot.Value;

public class Console extends InternalModule {
  public Console() {
    super("cauldron", true);
  }

  public void log(Value... contents) {
    for (Value value : contents) {
      // log
    }
  }

  public void error(Value... contents) {
    for (Value value : contents) {
      // log
    }
  }

  public void debug(Value... contents) {
    for (Value value : contents) {
      // log
    }
  }

  public void warn(Value... contents) {
    for (Value value : contents) {
      // log
    }
  }
}