package me.conji.cauldron.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import org.bukkit.Bukkit;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.internal.modules.Console;

public class Isolate {
  private static Isolate activeIsolate;

  private Cauldron cauldron;
  private Context context;
  private boolean initialized = false;
  private HashMap<String, Value> boundValues = new HashMap<>();
  private ModuleManager moduleManager;

  private boolean isEngaged = false;
  private SynchronousQueue<Value> asyncQueue = new SynchronousQueue<>(true);
  private int asyncProcessId;

  /**
   * Represents an instance of the VM that runs scripts. Objects located in one
   * isolate are not to be used in another isolate.
   * 
   * @param cauldronInstance
   */
  public Isolate(Cauldron cauldronInstance) {
    this.cauldron = cauldronInstance;
    this.moduleManager = new ModuleManager(this);
    this.context = Context.newBuilder("js").option("js.ecmascript-version", "10").allowHostAccess(HostAccess.ALL)
        .allowCreateThread(false).allowHostClassLoading(true).allowIO(false).build();
  }

  private Runnable getAsyncRunnable() {
    return new Runnable() {

      @Override
      public void run() {
        int processed = 0;
        while ((processed++) < 10 && !Isolate.activeIsolate.asyncQueue.isEmpty()) {
          try {
            Value nextInQueue = Isolate.activeIsolate.asyncQueue.take();
            nextInQueue.executeVoid();
          } catch (InterruptedException ex) {
            Console.debug("Failed to finish async queue due to interruption. Details below:", ex);
            break;
          }
        }
      }
    };
  }

  private void activate() {
    this.context.enter();
    if (!this.initialized) {
      this.moduleManager.registerModules();
      this.put("__isolate__", this);
      this.initialized = true;
    }
    activeIsolate = this;
    this.asyncProcessId = Bukkit.getScheduler().scheduleSyncRepeatingTask(cauldron, this.getAsyncRunnable(),
        this.asyncQueue.isEmpty() ? 10 : 2, 250);
  }

  private void pause() {
    Bukkit.getScheduler().cancelTask(this.asyncProcessId);
    this.context.leave();
  }

  /**
   * Scopes this Isolate to the current isolate;
   */
  public void scope() {
    if (activeIsolate != null) {
      // deactivate (not dispose) current isolate
      activeIsolate.pause();
    }
    this.activate();
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
    this.isEngaged = true;
    try {
      Source source = Source.newBuilder("js", script, location).build();
      this.isEngaged = false;
      return this.context.eval(source);
    } catch (IOException ex) {
      this.isEngaged = false;
      Console.error(ex);
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

  public ModuleManager getModuleManager() {
    return this.moduleManager;
  }
}