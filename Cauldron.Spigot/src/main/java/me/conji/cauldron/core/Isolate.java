package me.conji.cauldron.core;

import java.io.IOException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import me.conji.cauldron.Cauldron;

public class Isolate {
  private static Isolate activeIsolate;

  private Cauldron cauldron;
  private Context context;
  private AsyncContainer asyncContainer;

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
    this.asyncContainer = new AsyncContainer(this);
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

  /**
   * Gets the context of this isolate
   * 
   * @return
   */
  public Context getContext() {
    return this.context;
  }
}