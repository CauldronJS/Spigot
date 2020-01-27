package me.conji.cauldron.internal.modules;

import me.conji.cauldron.api.Module;

import me.conji.cauldron.api.JsAccess;

@JsAccess.INNER_BINDING("native_module")
public abstract class NativeModule extends Module {
  @Override
  public boolean isInternal() {
    return true;
  }

  @Override
  public String toString() {
    return "[NativeModule " + this.id + "]";
  }
}