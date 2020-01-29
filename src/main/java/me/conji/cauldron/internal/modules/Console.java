package me.conji.cauldron.internal.modules;

import java.util.Arrays;
import java.util.logging.Level;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.api.JsAccess;

@JsAccess.GLOBAL("console")
public class Console extends NativeModule {
  public Console() {
    super();
  }

  private static void l(Level level, Object... content) {
    for (Object item : content) {
      if (item.getClass().isArray()) {
        String result = "";
        Object[] arr = (Object[]) item;
        for (Object obj : arr) {
          result += System.lineSeparator() + "\t" + obj.toString();
        }
        Cauldron.instance().getLogger().log(level, result);
      } else {
        Cauldron.instance().getLogger().log(level, item.toString());
      }
    }
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