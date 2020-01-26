package me.conji.cauldron.internal.modules;

import java.util.Arrays;
import java.util.logging.Level;

import me.conji.cauldron.Cauldron;

public class Console extends NativeModule {
  public Console() {
    super();
  }

  private static void l(Level level, Object... content) {
    Cauldron.instance().getLogger().log(level, Arrays.toString(content));
  }

  public static void log(Object... contents) {
    l(Level.INFO, contents);
  }

  public static void error(Object... contents) {
    l(Level.SEVERE, contents);
  }

  public static void debug(Object... contents) {
    if (Cauldron.instance().getIsDebugging()) {
      l(Level.FINE, contents);
    }
  }

  public static void warn(Object... contents) {
    l(Level.WARNING, contents);
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