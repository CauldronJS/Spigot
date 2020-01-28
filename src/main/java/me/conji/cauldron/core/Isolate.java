package me.conji.cauldron.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import org.bukkit.Bukkit;
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

  private static final String ENGINE_ENTRY = "lib/internal/bootstrap/loader.js";

  private static final int POLLING_TIME = 2;
  private static final int POLLING_TIME_EMPTY = 10;
  private static final int POLLING_DURATION = 5;

  private static Isolate activeIsolate;
  private static ClassPath classPath;

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
        .allowHostAccess(HostAccess.ALL).allowCreateThread(true).allowHostClassLoading(true).allowIO(false).build();
    try {
      classPath = ClassPath.from(Cauldron.class.getClassLoader());
    } catch (IOException ex) {
      Console.error("Failed to register class path for Isolate.");
      Bukkit.getPluginManager().disablePlugin(Cauldron.instance());
    }

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

    ImmutableSet<ClassPath.ClassInfo> classes = classPath.getAllClasses();
    classes.forEach(classInfo -> {
      Class<?> clazz = classInfo.load();
      if (clazz.getAnnotation(JsAccess.GLOBAL.class) != null) {
        JsAccess.GLOBAL jsAccess = clazz.getAnnotation(JsAccess.GLOBAL.class);
        if (jsAccess.access() == JsAccess.AccessType.STATIC) {
          this.put(jsAccess.value(), clazz);
        } else {
          try {
            this.put(jsAccess.value(), clazz.newInstance());
          } catch (Exception ex) {
            Console.warn("Failed to bind type " + clazz.getName(), ex);
          }
        }
      } else if (clazz.getAnnotation(JsAccess.INNER_BINDING.class) != null) {
        JsAccess.INNER_BINDING jsAccess = clazz.getAnnotation(JsAccess.INNER_BINDING.class);
        String prefixedName = CAULDRON_SYMBOL + ".internal." + jsAccess.value();
        if (jsAccess.access() == JsAccess.AccessType.STATIC) {
          this.put(prefixedName, clazz);
        } else {
          try {
            this.put(prefixedName, clazz.newInstance());
          } catch (Exception ex) {
            Console.warn("Failed to bind type " + clazz.getName(), ex);
          }
        }
      } else if (clazz.getAnnotation(JsAccess.BINDING.class) != null) {
        JsAccess.BINDING jsAccess = clazz.getAnnotation(JsAccess.BINDING.class);
        String prefixedName = CAULDRON_SYMBOL + ".public." + jsAccess.value();
        if (jsAccess.access() == JsAccess.AccessType.STATIC) {
          this.put(prefixedName, clazz);
        } else {
          try {
            this.put(prefixedName, clazz.newInstance());
          } catch (Exception ex) {
            Console.warn("Failed to bind type " + clazz.getName(), ex);
          }
        }
      }
    });
  }

  private boolean activate() {
    this.context.enter();
    if (!this.initialized) {
      this.createBindings();
      this.moduleManager.registerModules();
      try {
        this.runScript(FileReader.read(ENGINE_ENTRY), ENGINE_ENTRY);
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