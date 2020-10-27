package com.cauldronjs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.graalvm.polyglot.Value;

public class BukkitBridge {
  public void registerNewEventHandler(Plugin cauldron, String type, Value handler) throws ClassNotFoundException {
    registerNewEventHandler(cauldron, Class.forName(type).asSubclass(Event.class), handler);
  }

  /**
   * Registers an event handler for the specific event type
   * 
   * @param type    Type
   * @param handler
   * @throws ClassNotFoundException
   */
  public void registerNewEventHandler(Plugin cauldron, Class<? extends Event> type, Value handler)
      throws ClassNotFoundException {
    Listener lis = new Listener() {
      public int hashCode() {
        return super.hashCode();
      }
    };
    EventExecutor exec = (listener, event) -> {
      if (event.isAsynchronous()) {
        // send to Isolate's thread and process on said thread.
      } else {
        handler.execute(event);
      }
    };

    Bukkit.getPluginManager().registerEvent(type, lis, EventPriority.NORMAL, exec, cauldron);
  }

  /**
   * Creates a command bound to Cauldron
   */
  public Command createCommand(String name, final Value handler) {
    return new Command(name) {
      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return handler.execute(sender, commandLabel, args).asBoolean();
      }
    };
  }
}