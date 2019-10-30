package me.conji.cauldron.api.js;

public interface Environment {
  public Object registerCommand(String name, Object args);

  public void unregisterCommand(String name);

  public void clearCommands();
}