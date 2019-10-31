package me.conji.cauldron;

import me.conji.cauldron.api.CauldronApi;
import me.conji.cauldron.api.js.Environment;
import org.graalvm.polyglot.Value;

public class SpigotEnvironment implements Environment {
  private CauldronApi api;

  public SpigotEnvironment(CauldronApi api) {
    this.api = api;
  }

  @Override
  public Object registerCommand(String name, Object args) {
    return null;
  }

  @Override
  public void unregisterCommand(String name) {

  }

  @Override
  public void clearCommands() {

  }

  @Override
  public void registerEventHandler(String type, Value handler) throws ClassNotFoundException {

  }
}
