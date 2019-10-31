package me.conji.cauldron.api;

import me.conji.cauldron.api.async.AsyncFunction;

public interface ProcessRunner {
  public AsyncFunction run(String command, String dir);
}