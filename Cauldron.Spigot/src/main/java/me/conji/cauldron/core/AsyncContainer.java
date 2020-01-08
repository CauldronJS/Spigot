package me.conji.cauldron.core;

import java.util.concurrent.SynchronousQueue;

import org.bukkit.Bukkit;
import org.graalvm.polyglot.Value;

public class AsyncContainer {
  Isolate isolate;
  SynchronousQueue<Value> fnQueue = new SynchronousQueue<>();
  boolean isRunning = false;

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
    this.isRunning = true;
    while (!this.fnQueue.isEmpty()) {
      try {
        Value fn = this.fnQueue.take();
        fn.execute(this.isolate.getContext().getPolyglotBindings()); // global

      } catch (InterruptedException ex) {
        // do nothing
      }
    }
    this.isRunning = false;
  }

  public boolean isRunning() {
    return this.isRunning;
  }
}