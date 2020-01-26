package me.conji.cauldron.internal.modules;

import me.conji.cauldron.api.Module;
import me.conji.cauldron.core.Isolate;

public abstract class NativeModule extends Module {
  static boolean exists(String id) {
    return getCached(id) != null;
  }

  static NativeModule getCached(String id) {
    return (NativeModule) Isolate.activeIsolate().getModuleManager().getInternalModule(id);
  }

  static String wrapBody(String body) {
    return "(function (exports, module, require, process, plugin, NativeModule) {" + body + "\n})";
  }

  @Override
  public boolean isInternal() {
    return true;
  }

  @Override
  public String toString() {
    return "[NativeModule " + this.id + "]";
  }
}