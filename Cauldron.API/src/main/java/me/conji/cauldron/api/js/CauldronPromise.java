package me.conji.cauldron.api.js;

import me.conji.cauldron.api.async.AsyncFunction;
import org.graalvm.polyglot.Value;

public abstract class CauldronPromise implements AsyncFunction {
  private Object lock;
  private Value function;

  protected CauldronPromise(Value function) {
    this.lock = new Object();
    this.function = function;
  }

  public Value toNativePromise() {
    return null;
  }

  public abstract boolean isBlocking();

  public Object getLock() {
    return this.lock;
  }

  @Override
  public Value getFunction() {
    return this.function;
  }

  public Value function() {
    return null;
  }

  @Override
  public void run() {

  }

  public CauldronPromise doThen(Value handle) {
    return null;
  }

  public CauldronPromise doCatch(Value handle) {
    return null;
  }
}