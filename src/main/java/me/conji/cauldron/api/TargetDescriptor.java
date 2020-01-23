package me.conji.cauldron.api;

import java.util.HashMap;

/**
 * Describes the current Cauldron implementation
 */
public class TargetDescriptor {
  /**
   * The platform Cauldron is running on (spigot, bukkit, sponge, etc)
   */
  private String platform;
  /**
   * Describes the versions of the platform
   */
  private HashMap<String, String> versions = new HashMap<>();

  public TargetDescriptor(String platform) {
    this.platform = platform;
  }

  public void addVersionDescription(String key, String value) {
    this.versions.put(key, value);
  }

  public String getPlatform() {
    return this.platform;
  }

  public HashMap<String, String> getVersions() {
    return this.versions;
  }
}