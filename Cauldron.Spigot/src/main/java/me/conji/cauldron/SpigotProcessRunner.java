package me.conji.cauldron;

import me.conji.cauldron.api.ProcessRunner;
import me.conji.cauldron.api.js.CauldronPromise;
import org.graalvm.polyglot.Value;

public class SpigotProcessRunner implements ProcessRunner {
  @Override
  public CauldronPromise run(String command, String dir) {
    return new CauldronPromise(null) {
      @Override
      public boolean isBlocking() {
        return false;
      }

      @Override
      public Value function() {
        return null;
      }
    };
  }
}
