package me.conji.cauldron;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.*;

import me.conji.cauldron.api.TargetDescriptor;
import me.conji.cauldron.core.FileReader;
import me.conji.cauldron.core.Isolate;
import me.conji.cauldron.internal.modules.Console;

public class Cauldron extends JavaPlugin {
  private static Cauldron instance;
  private final String ENGINE_ENTRY = "lib/internal/bootstrap/loaders.js";

  private Isolate mainIsolate;
  private TargetDescriptor targetDescriptor;

  public Cauldron() {
    this.targetDescriptor = new TargetDescriptor("spigot");
    this.targetDescriptor.addVersionDescription("nms", this.getNMSVersion());
    this.targetDescriptor.addVersionDescription("cb", this.getCBVersion());
  }

  @Override
  public void onEnable() {
    instance = this;
    this.mainIsolate = new Isolate(this);
    // load the entry file
    try {
      String entry = FileReader.read(ENGINE_ENTRY);
      this.mainIsolate.runScript(entry, ENGINE_ENTRY);
    } catch (FileNotFoundException ex) {
      Console.error("Failed to find Cauldron entry point", ex);
    } catch (IOException ex) {
      Console.error("An error occured when loading Cauldron", ex);
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

  public static Cauldron instance() {
    return instance;
  }
}
