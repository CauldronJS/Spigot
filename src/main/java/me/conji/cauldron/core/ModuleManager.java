package me.conji.cauldron.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.reflections.Reflections;

import me.conji.cauldron.api.Module;
import me.conji.cauldron.internal.modules.InternalModule;

public class ModuleManager {
  private Isolate isolate;
  private HashMap<String, InternalModule> internalModules = new HashMap<>();
  private HashMap<String, Module> publicModules = new HashMap<>();

  /**
   * ModuleManagers are in charge of Java specific modules that are typically
   * created in the V8 engine (i.e. internal buffer implementation, promises,
   * console, etc)
   * 
   * @param isolate
   */
  public ModuleManager(Isolate isolate) {
    this.isolate = isolate;
    Set<Class<? extends Module>> modules = new Reflections("me.conji.cauldron").getSubTypesOf(Module.class);
    modules.forEach(clazz -> {
      try {
        Module module = clazz.newInstance();
        if (module.isInternal()) {
          this.internalModules.put(module.getName(), (InternalModule) module);
        } else {
          this.publicModules.put(module.getName(), module);
        }
        if (module.isGlobal()) {
          this.isolate.put(module.getName(), module);
        }
      } catch (InstantiationException instantiationException) {
        this.isolate.cauldron().getLogger().log(Level.SEVERE, "Failed to instantiante module " + clazz.getName());
      } catch (IllegalAccessException illegalAccessException) {
        this.isolate.cauldron().getLogger().log(Level.SEVERE, "Failed to access module " + clazz.getName());
      }
    });
  }

  public void injectInternalModules() {

  }
}