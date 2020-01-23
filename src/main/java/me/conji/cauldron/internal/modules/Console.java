package me.conji.cauldron.internal.modules;

import org.graalvm.polyglot.Value;

public class Console extends InternalModule {
  public Console() {
    super();
  }

  public static void log(Object... contents) {
    for (Object value : contents) {
      // log
    }
  }

  public static void error(Object... contents) {
    for (Object value : contents) {
      // log
    }
  }

  public static void debug(Object... contents) {
    for (Object value : contents) {
      // log
    }
  }

  public static void warn(Object... contents) {
    for (Object value : contents) {
      // log
    }
  }

  @Override
  public String getName() {
    return "console";
  }

  @Override
  public boolean isGlobal() {
    return true;
  }
}