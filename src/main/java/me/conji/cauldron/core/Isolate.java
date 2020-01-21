package me.conji.cauldron.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import me.conji.cauldron.Cauldron;

public class Isolate {
  private static Isolate activeIsolate;

  private Cauldron cauldron;
  private Context context;
  private ModuleManager modules;
  private HashMap<String, Value> boundValues = new HashMap<>();

  /**
   * Represents an instance of the VM that runs scripts. Objects located in one
   * isolate are not to be used in another isolate.
   * 
   * @param cauldronInstance
   */
  public Isolate(Cauldron cauldronInstance) {
    this.cauldron = cauldronInstance;
    this.context = Context.newBuilder("js").option("js.ecmascript-version", "10").allowHostAccess(HostAccess.ALL)
        .allowCreateThread(false).allowHostClassLoading(true).allowIO(false).build();
    this.modules = new ModuleManager(this);
  }

  /**
   * Scopes this Isolate to the current isolate;
   */
  public void scope() {
    if (activeIsolate != null) {
      // deactivate (not dispose) current isolate
      activeIsolate.context.leave();
    }
    activeIsolate = this;
    this.context.enter();
  }

  /**
   * Disposes of the isolate, destroying any resources and freeing them
   */
  public void dispose() {
    this.context.close(true);
  }

  /**
   * Returns the current isolate
   * 
   * @return
   */
  public static Isolate activeIsolate() {
    return activeIsolate;
  }

  /**
   * Returns the Cauldron instance this Isolate belongs to
   * 
   * @return
   */
  public Cauldron cauldron() {
    return this.cauldron;
  }

  /**
   * Runs the script against this isolate
   * 
   * @param script
   * @param location
   * @return
   */
  public Value runScript(String script, String location) {
    try {
      Source source = Source.newBuilder("js", script, location).build();
      return this.context.eval(source);
    } catch (IOException ex) {
      // log
      return null;
    }
  }

  public void put(String identifier, Object object) {
    this.context.getPolyglotBindings().putMember(identifier, object);
  }

  public void bind(String identifier, Object value) {
    this.boundValues.put(identifier, Value.asValue(value));
  }

  /**
   * Gets the context of this isolate
   * 
   * @return
   */
  public Context getContext() {
    return this.context;
  }

  public boolean queueAsync(Value fn) {
    return this.asyncContainer.queue(fn);
  }
}