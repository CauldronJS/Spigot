package me.conji.cauldron.api;

import org.graalvm.polyglot.Value;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

public class CauldronVM {
  private HashMap<String, Object> environmentVariables;
  private LanguageEngine engine;

  public CauldronVM(LanguageEngine engine) {
    this.environmentVariables = new HashMap<>();
    this.engine = engine;
  }

  public Object get(String name) {
    return this.environmentVariables.get(name);
  }

  public void put(String name, Object value) {
    if (this.engine.isInitialized()) {
      throw new AccessControlException("Cannot edit VM variables once the engine has been initialized");
    }
    this.environmentVariables.put(name, value);
  }

  public Value asPolyglotObject() {
    return Value.asValue(this.environmentVariables);
  }
}
