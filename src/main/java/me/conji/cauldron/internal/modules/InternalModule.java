package me.conji.cauldron.internal.modules;

import me.conji.cauldron.api.Module;

public abstract class InternalModule extends Module {
  @Override
  public boolean isInternal() {
    return true;
  }
}