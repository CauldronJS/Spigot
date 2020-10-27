package com.cauldronjs;

import java.io.File;
import java.util.logging.Level;

import com.cauldronjs.isolate.Isolate;
import com.cauldronjs.isolate.IsolateManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.graalvm.polyglot.Value;

public class Cauldron extends JavaPlugin implements CauldronAPI {
  private static final int PLUGIN_ID = 6842;

  private static Cauldron instance;

  private Isolate mainIsolate;
  private IsolateManager isolateManager;
  private TargetDescriptor targetDescriptor;
  private boolean isInDebugMode = true;

  public Cauldron() {
    instance = this;
    this.targetDescriptor = new TargetDescriptor("spigot", this.getNMSVersion());
  }

  @Override
  public void onEnable() {
    instance = this;
    this.isolateManager = new IsolateManager(this);
    this.mainIsolate = this.isolateManager.initialize();
    this.mainIsolate.getBindingProvider().register(new BukkitBridge());
    this.isolateManager.activateIsolate(this.mainIsolate);
    // load the entry file
    this.mainIsolate.start();
    this.log(Level.INFO, "Finished initializing Cauldron");

    Metrics metrics = new Metrics(this, PLUGIN_ID);

  }

  @Override
  public void onDisable() {
    // dispose of engine and wait for all promises to finish
    try {
      // TODO: the reload command attempts to fetch JS commands, gotta figure
      // out how to prevent this from haulting the reload
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
  public IsolateManager getIsolateManager() {
    return this.isolateManager;
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
  public int scheduleRepeatingTask(Value fn, int interval, int timeout) {
    Runnable runnable = fn::executeVoid;
    return Bukkit.getScheduler().runTaskTimer(this, runnable, interval / 50, timeout / 50).getTaskId();
  }

  @Override
  public int scheduleTask(Value fn, int timeout) {
    Runnable runnable = fn::executeVoid;
    if (timeout == 0) {
      return Bukkit.getScheduler().runTask(this, runnable).getTaskId();
    } else {
      return Bukkit.getScheduler().runTaskLater(this, runnable, timeout / 50).getTaskId();
    }
  }

  @Override
  public int scheduleRepeatingTask(Runnable runnable, int interval, int timeout) {
    return Bukkit.getScheduler().runTaskTimer(this, runnable, interval / 50, timeout / 50).getTaskId();
  }

  @Override
  public int scheduleTask(Runnable runnable, int timeout) {
    if (timeout == 0) {
      return Bukkit.getScheduler().runTask(this, runnable).getTaskId();
    } else {
      return Bukkit.getScheduler().runTaskLater(this, runnable, timeout / 50).getTaskId();
    }
  }

  @Override
  public File getDefaultCwd() {
    return this.getDataFolder();
  }

  @Override
  public boolean isRunning() {
    return true;
  }
}
