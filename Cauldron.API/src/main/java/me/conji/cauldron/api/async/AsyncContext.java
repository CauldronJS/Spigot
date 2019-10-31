package me.conji.cauldron.api.async;

import org.graalvm.polyglot.Value;

public interface AsyncContext {
  public AsyncFunction register(Value function);
}