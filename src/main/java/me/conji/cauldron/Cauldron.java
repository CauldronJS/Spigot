package me.conji.cauldron;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.conji.cauldron.api.TargetDescriptor;
import me.conji.cauldron.core.FileReader;
import me.conji.cauldron.core.Isolate;
import me.conji.cauldron.internal.modules.Console;

public class Cauldron extends JavaPlugin {
  private static Cauldron instance;
  private final String ENGINE_ENTRY = "lib/internal/bootstrap/loader.js";

  private Isolate mainIsolate;
  private TargetDescriptor targetDescriptor;
  private boolean isInDebugMode = false;

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
      this.mainIsolate.scope();
      String entry = FileReader.read(ENGINE_ENTRY);
      this.mainIsolate.runScript(entry, ENGINE_ENTRY);
      Console.log("Finished initializing Cauldron");
    } catch (FileNotFoundException ex) {
      Console.error("Failed to find Cauldron entry point", ex);
    } catch (IOException ex) {
      Console.error("An error occured when loading Cauldron", ex);
    }
  }

  @Override
  public void onDisable() {
    // dispose of engine and wait for all promises to finish
    this.mainIsolate.dispose();
  }

  public void setIsDebugging(boolean value) {
    this.isInDebugMode = value;
  }

  public boolean getIsDebugging() {
    return this.isInDebugMode;
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
