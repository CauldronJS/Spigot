package me.conji.cauldron;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.conji.cauldron.api.TargetDescriptor;
import me.conji.cauldron.core.Isolate;
import me.conji.cauldron.internal.modules.Console;

public class Cauldron extends JavaPlugin {
  private static Cauldron instance;

  private Isolate mainIsolate;
  private TargetDescriptor targetDescriptor;
  private boolean isInDebugMode = false;

  public Cauldron() {
    instance = this;
    this.targetDescriptor = new TargetDescriptor("spigot");
    this.targetDescriptor.addVersionDescription("nms", this.getNMSVersion());
    this.targetDescriptor.addVersionDescription("cb", this.getCBVersion());
  }

  @Override
  public void onEnable() {
    instance = this;
    if (!this.getDataFolder().exists()) {
      this.getDataFolder().mkdirs();
      Path datadir = this.getDataFolder().toPath();
      try {
        datadir.resolve("src").toFile().mkdir();
        Files.copy(this.getResource("package.json"), datadir.resolve("package.json"));
        Files.copy(this.getResource("src/index/js"), datadir.resolve("src/index"));
      } catch (IOException ex) {
        // ignore
      }
    }
    this.mainIsolate = new Isolate(this);
    // load the entry file
    this.mainIsolate.scope();
    Console.log("Finished initializing Cauldron");
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

  public Isolate isolate() {
    return this.mainIsolate;
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
