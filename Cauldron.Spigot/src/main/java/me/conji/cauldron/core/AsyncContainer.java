package me.conji.cauldron.core;

import java.util.concurrent.SynchronousQueue;

import org.bukkit.Bukkit;
import org.graalvm.polyglot.Value;

public class AsyncContainer {
  Isolate isolate;
  SynchronousQueue<Value> fnQueue = new SynchronousQueue<>();

  public AsyncContainer(Isolate isolate) {
    this.isolate = isolate;
  }

  public boolean queue(Value fn) {
    if (fn.canExecute()) {
      return this.fnQueue.add(fn);
    } else {
      return false;
    }
  }

  public void run() {
    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() < (startTime + 500) && !this.fnQueue.isEmpty()) {
      // we don't want it running anymore than 500ms, though that may be too long as
      // well
      try {
        Value fn = this.fnQueue.take();
        fn.execute(this.isolate.getContext().getPolyglotBindings()); // global

      } catch (InterruptedException ex) {
        // do nothing
      }
    }
  }
}