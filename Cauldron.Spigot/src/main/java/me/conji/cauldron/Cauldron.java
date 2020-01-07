package me.conji.cauldron;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.*;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;

public class Cauldron extends JavaPlugin {
  private static Cauldron instance;

  private Reflections reflections;

  @Override
  public void onEnable() {
    instance = this;
    this.reflections = new Reflections("me.conji.cauldron");
    this.engine = new JavaScriptEngine(this);
    this.vm = new CauldronVM(this.engine);
    this.environment = new SpigotEnvironment(this);
    this.vm.put("target", new TargetDescriptor("Spigot", this.getMCVersion()));
    try {
      this.engine.initialize(this.getDataFolder());
      this.asyncContext = new SpigotAsyncContext();
      InputStream loader = this.engine.getFile("lib/internal/bootstrap/loaders.js");
      this.engine.eval(loader, "lib/internal/bootstrap/loaders.js");

    } catch (IOException ex) {
      this.getLogger().log(Level.SEVERE, "Failed to bootstrap Cauldron: " + ex.getMessage());
    }
  }

  @Override
  public void onDisable() {
    // dispose of engine and wait for all promises to finish
  }

  public static void registerNewEventHandler(String type, Value handler) throws ClassNotFoundException {
    Class clazz = Class.forName(type);
    Listener lis = new Listener() {
      public int hashCode() {
        return super.hashCode();
      }
    };
    EventExecutor exec = (listener, event) -> {
      try {
        handler.execute(event);
      } catch (Exception exception) {
        // do nothing
      }
    };

    Bukkit.getPluginManager().registerEvent(clazz, lis, EventPriority.NORMAL, exec, instance);
  }

  public static Command createCommand(String name, final Value handler) {
    return new Command(name) {
      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return handler.execute(sender, commandLabel, args).asBoolean();
      }
    };
  }

  @Override
  public Path getPath() {
    return this.getDataFolder().toPath();
  }

  @Override
  public CauldronVM getVM() {
    return this.vm;
  }

  @Override
  public AsyncContext getAsyncContext() {
    return this.asyncContext;
  }

  @Override
  public ProcessRunner getProcessRunner() {
    return null;
  }

  @Override
  public LanguageEngine getEngine() {
    return this.engine;
  }

  @Override
  public Environment getEnvironment() {
    return this.environment;
  }

  public Reflections getReflections() {
    return reflections;
  }

  private String getMCVersion() {
    return Bukkit.getVersion();
  }

  private String getNMSVersion() {
    String fullName = Bukkit.getServer().getClass().getPackage().getName();
    String withUnderscores = fullName.substring(fullName.lastIndexOf('.') + 1);
    if (withUnderscores.startsWith("v_")) {
      return withUnderscores.substring(2);
    } else {
      return withUnderscores.substring(1);
    }
  }

  private String getCBVersion() {
    return Bukkit.getBukkitVersion();
  }
}
