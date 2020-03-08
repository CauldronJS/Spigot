package com.cauldronjs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.cauldronjs.utils.Console;
import com.cauldronjs.utils.PathHelpers;

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
    try {
      this.mainIsolate = new Isolate(this);
      this.mainIsolate.bind("BukkitBridge", new BukkitBridge());
      // load the entry file
      this.mainIsolate.scope();
      this.log(Level.INFO, "Finished initializing Cauldron");
    } catch (IOException ex) {
      this.log(Level.WARNING, "Failed to instantiate cwd");
    }
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
    return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, interval / 40, timeout / 40);
  }

  @Override
  public int scheduleTask(Runnable runnable, int timeout) {
    return Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable, timeout / 40);
  }

  @Override
  public File getDefaultCwd() {
    return this.getDataFolder();
  }
}
