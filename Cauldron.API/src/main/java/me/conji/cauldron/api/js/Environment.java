package me.conji.cauldron.api.js;

import org.graalvm.polyglot.Value;

public interface Environment {
  public Object registerCommand(String name, Object args);

  public void unregisterCommand(String name);

  public void clearCommands();

  public void registerEventHandler(String type, Value handler) throws ClassNotFoundException;
}