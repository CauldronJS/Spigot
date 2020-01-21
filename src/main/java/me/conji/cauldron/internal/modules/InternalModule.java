package me.conji.cauldron.internal.modules;

import me.conji.cauldron.api.Module;

public abstract class InternalModule extends Module {
  String name;
  boolean isGlobal;

  public InternalModule(String name, boolean isGlobal) {
    super();
    this.name = name;
    this.isGlobal = isGlobal;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public boolean isInternal() {
    return true;
  }

  public boolean isGlobal() {
    return this.isGlobal;
  }
}