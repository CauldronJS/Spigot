package me.conji.cauldron.api;

import me.conji.cauldron.core.Isolate;

public abstract class Module {
  private Isolate isolate;

  public Module() {
    this.isolate = Isolate.activeIsolate();
  }

  public abstract String getName();

  public boolean isInternal() {
    return false;
  }

  public abstract boolean isGlobal();

  public Isolate getIsolate() {
    return this.isolate;
  }
}