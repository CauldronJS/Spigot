package me.conji.cauldron.core;

import java.util.HashMap;

import me.conji.cauldron.api.Module;
import me.conji.cauldron.internal.modules.Console;
import me.conji.cauldron.utils.PathHelpers;

public class ModuleManager {
  private Isolate isolate;
  private HashMap<String, Object> registeredModules = new HashMap<>();
  private HashMap<String, Object> internalModules = new HashMap<>();

  /**
   * ModuleManagers are in charge of Java specific modules that are typically
   * created in the V8 engine (i.e. internal buffer implementation, promises,
   * console, etc)
   * 
   * @param isolate
   */
  public ModuleManager(Isolate isolate) {
    this.isolate = isolate;
  }

  public void registerModules() {
    this.putModule(Console.class, PathHelpers.class);
  }

  public Object getModule(String id) {
    return this.registeredModules.get(id);
  }

  public Object getInternalModule(String id) {
    return this.internalModules.get(id);
  }

  @SuppressWarnings("unchecked")
  public void putModule(Object... modules) {
    for (Object obj : modules) {
      try {
        Module module = null;
        if (obj instanceof Module) {
          module = (Module) obj;
        } else if (obj instanceof Class) {
          module = ((Class<? extends Module>) obj).newInstance();
        }
        if (module.isGlobal() && module.getName() != null) {
          // injects as a global class
          this.isolate.bind(module.getName(), obj);
        }
        if (module.isInternal()) {
          // bind with access to `internalBinding`
          this.internalModules.put(module.id, obj);
        } else {
          this.registeredModules.put(module.id, obj);
        }
      } catch (Exception ex) {
        Console.error(ex);
      }
    }
  }

  public void clear() {
    this.registeredModules.clear();
  }
}