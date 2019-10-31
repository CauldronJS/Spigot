package me.conji.cauldron.api.async;


import org.graalvm.polyglot.Value;

public interface AsyncFunction extends Runnable {
  public Value getFunction();
}
