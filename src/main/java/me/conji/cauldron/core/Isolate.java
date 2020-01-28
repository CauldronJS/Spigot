package me.conji.cauldron.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

<<<<<<< HEAD
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

=======
>>>>>>> 01035ee9c55d5e90b17577994d52f997a8491ae6
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.api.JsAccess;
import me.conji.cauldron.internal.modules.Console;

@JsAccess.INNER_BINDING("isolate")
public class Isolate {
  private static final String CAULDRON_SYMBOL = "$$cauldron$$";
<<<<<<< HEAD
  private static final String PLUGIN_SYMBOL = CAULDRON_SYMBOL + ".plugin";
=======
  private static final String ISOLATE_SYMBOL = CAULDRON_SYMBOL + ".isolate";
>>>>>>> 01035ee9c55d5e90b17577994d52f997a8491ae6

  private static final String ENGINE_ENTRY = "lib/internal/bootstrap/loader.js";

  private static final int POLLING_TIME = 2;
  private static final int POLLING_TIME_EMPTY = 10;
  private static final int POLLING_DURATION = 5;

  private static Isolate activeIsolate;

  private Cauldron cauldron;
  private Context context;
  private boolean initialized = false;
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
    this.context = Context.newBuilder("js").option("js.ecmascript-version", "10").allowAllAccess(true)
<<<<<<< HEAD
        .allowHostAccess(HostAccess.ALL).allowCreateThread(true).allowHostClassLoading(true).allowIO(false).build();
    try {
      classPath = ClassPath.from(Cauldron.class.getClassLoader());
    } catch (IOException ex) {
      Console.error("Failed to register class path for Isolate.");
      Bukkit.getPluginManager().disablePlugin(Cauldron.instance());
    }
  }

  private static HashMap<String, ClassPath> getAllClassPaths() {
    HashMap<String, ClassPath> results = new HashMap<>();
    Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
    for (Plugin plugin : plugins) {
      try {
        results.put(plugin.getName(), ClassPath.from(plugin.getClass().getClassLoader()));
      } catch (IOException ex) {
        Console.warn("Failed to register class paths for plugin " + plugin.getName(), ex);
      }
    }
    return results;
=======
        .allowHostAccess(HostAccess.ALL).allowHostClassLoading(true).allowHostClassLookup(s -> true).build();
>>>>>>> 01035ee9c55d5e90b17577994d52f997a8491ae6
  }

  private Runnable getAsyncRunnable() {
    return new Runnable() {

      @Override
      public void run() {
        int processed = 0;
        Isolate isolate = Isolate.activeIsolate;
        while ((processed++) < 10 && !isolate.asyncQueue.isEmpty() && isolate.isEngaged) {
          try {
            Value nextInQueue = isolate.asyncQueue.take();
            nextInQueue.executeVoid();
          } catch (InterruptedException ex) {
            Console.debug("Failed to finish async queue due to interruption. Details below:", ex);
            break;
          }
        }
      }
    };
  }

  private void createBindings() {
    // polyfill globalThis if the current version doesn't have it
    this.put("globalThis", this.context.getPolyglotBindings());
    this.put("process", false);
    this.put(CAULDRON_SYMBOL, this.cauldron);
    this.put("bukkit_bridge", BukkitBridge.class);
  }

  private boolean activate() {
    this.context.enter();
    if (!this.initialized) {
      this.createBindings();
      try {
        this.runScript(FileReader.read(this.cauldron, ENGINE_ENTRY), ENGINE_ENTRY);
        this.initialized = true;
      } catch (FileNotFoundException ex) {
        Console.error("Failed to find Cauldron entry point", ex);
        return false;
      } catch (IOException ex) {
        Console.error("An error occured while reading entry point", ex);
        return false;
      }
    }
    activeIsolate = this;
    // refresh the registered isolate
    this.put(ISOLATE_SYMBOL, this);
    this.asyncProcessId = Bukkit.getScheduler().scheduleSyncRepeatingTask(cauldron, this.getAsyncRunnable(),
        this.asyncQueue.isEmpty() ? POLLING_TIME_EMPTY : POLLING_TIME, POLLING_DURATION);
    return true;
  }

  private void pause() {
    Bukkit.getScheduler().cancelTask(this.asyncProcessId);
    this.context.leave();
  }

  /**
   * Scopes this Isolate to the current isolate;
   */
  public boolean scope() {
    if (activeIsolate != null) {
      // deactivate (not dispose) current isolate
      activeIsolate.pause();
    }
    return this.activate();
  }

  /**
   * Disposes of the isolate, destroying any resources and freeing them
   */
  public void dispose() {
    Bukkit.getScheduler().cancelTask(this.asyncProcessId);
    this.context.leave();
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

  public Value runScript(String location) throws FileNotFoundException, IOException {
    String content = FileReader.read(this.cauldron, location);
    return this.runScript(content, location);
  }

  public void put(String identifier, Object object) {
    this.context.getPolyglotBindings().putMember(identifier, object);
  }

  public void queueFn(Value fn) {
    if (fn.canExecute()) {
      this.asyncQueue.add(fn);
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

  public ModuleManager getModuleManager() {
    return this.moduleManager;
  }
}