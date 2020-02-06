package me.conji.cauldron;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.conji.cauldron.utils.Console;

public class Cauldron extends JavaPlugin implements CauldronAPI {
  private static Cauldron instance;

  private Isolate mainIsolate;
  private TargetDescriptor targetDescriptor;
  private boolean isInDebugMode = true;

  public Cauldron() {
    instance = this;
    this.targetDescriptor = new TargetDescriptor("spigot", this.getNMSVersion());
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
    try {
      // this.mainIsolate.dispose();
    } catch (Exception ex) {
      // ignore
    }
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

  public static Cauldron instance() {
    return instance;
  }

  @Override
  public Isolate getMainIsolate() {
    return this.mainIsolate;
  }

  @Override
  public boolean cancelTask(int id) {
    Bukkit.getScheduler().cancelTask(id);
    return true;
  }

  @Override
  public File cwd() {
    return this.getDataFolder();
  }

  @Override
  public TargetDescriptor getTarget() {
    return this.targetDescriptor;
  }

  @Override
  public boolean isDebugging() {
    return this.isInDebugMode;
  }

  @Override
  public void log(Level level, String msg) {
    this.getLogger().log(level, msg);
  }

  @Override
  public int scheduleRepeatingTask(Runnable runnable, int interval, int timeout) {
    return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, interval, timeout);
  }

  @Override
  public int scheduleTask(Runnable runnable, int timeout) {
    return Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable);
  }
}
