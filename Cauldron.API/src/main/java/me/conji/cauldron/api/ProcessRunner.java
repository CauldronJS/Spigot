package me.conji.cauldron.api;

import me.conji.cauldron.api.js.CauldronPromise;

public interface ProcessRunner {
  public CauldronPromise run(String command, String dir);
}