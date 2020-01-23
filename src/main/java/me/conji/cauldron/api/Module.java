package me.conji.cauldron.api;

import java.util.HashSet;

import org.graalvm.polyglot.Value;

import me.conji.cauldron.api.exceptions.NotImplementedException;
import me.conji.cauldron.core.Isolate;

public abstract class Module {
  private Isolate isolate;

  // These are public for JS access

  /**
   * The ID found in `require`
   */
  public String id;
  /**
   * The exports of the module
   */
  public Value exports;
  /**
   * The parent module of this module
   */
  public Module parent;
  /**
   * The filename this module belongs to. Each module is contained by only 1 file
   */
  public String filename;
  /**
   * Whether or not this module has been loaded
   */
  public boolean loaded = false;
  /**
   * The children modules this module owns. Any requires that use relative pathing
   * will note that this module is the parent that initialized it
   */
  public HashSet<Module> children = new HashSet<>();
  /**
   * The root directory this module lives in
   */
  public String rootDir = "";
  /**
   * The configuration object of this module, typically a package.json
   */
  public Value config;

  /**
   * Initializes an empty module for hydration, binding it to the current isolate
   */
  public Module() {
    this.isolate = Isolate.activeIsolate();
  }

  /**
   * Returns the name of the module, not the ID.
   * 
   * @implNote When the Buffer module is created, the ID would be 'buffer' so we
   *           can use `require('buffer')`, but since it's global, the name of the
   *           module would be 'Buffer' and injected as a property in the global
   *           scope
   * 
   * @return String
   */
  public String getName() throws NotImplementedException {
    if (this.isGlobal()) {
      throw new NotImplementedException("Module::getName()");
    } else {
      return null;
    }
  }

  /**
   * Returns whether or not the module is internal. Default is false.
   * 
   * @return boolean
   */
  public boolean isInternal() {
    return false;
  }

  /**
   * Returns whether or not the module is global. Global modules do not need an
   * import or require statement, like 'console' or 'buffer'
   * 
   * @return boolean
   */
  public abstract boolean isGlobal();

  /**
   * Gets the isolate that this module is bound to. Only once instance of a module
   * can exist per module and cannot be reused across another isolate
   * 
   * @return Isolate
   */
  public Isolate getIsolate() {
    return this.isolate;
  }
}