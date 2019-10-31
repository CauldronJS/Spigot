package me.conji.cauldron.api.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Map;

public abstract class CauldronPromise {
  private Object lock;

  protected CauldronPromise() {
    this.lock = new Object();
  }

  public Value toNativePromise() {
    return null;
  }

  public abstract boolean isBlocking();

  public Object getLock() {
    return this.lock;
  }
}