package me.conji.cauldron.api;

import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.security.AccessControlException;

public class CauldronVM {
  private LanguageEngine engine;
  private Value vmSymbol;

  public CauldronVM(LanguageEngine engine) {
    this.engine = engine;
    try {
      this.vmSymbol = engine.eval("return Symbol('CAULDRON_VM')", "CauldronVM");
    } catch (IOException ex) {
      // debug
    }
  }

  public Object get(String name) {
    return this.engine.get(name);
  }

  public void put(String name, Object value) {
    if (this.engine.isInitialized()) {
      throw new AccessControlException("Cannot edit VM variables once the engine has been initialized");
    }
    

  }

  public Value asPolyglotObject() {
    return this.engine.bindings();
  }
}
