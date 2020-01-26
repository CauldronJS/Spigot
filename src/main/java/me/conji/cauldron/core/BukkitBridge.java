package me.conji.cauldron.core;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.graalvm.polyglot.Value;

import me.conji.cauldron.Cauldron;

public class BukkitBridge {
  public static void registerNewEventHandler(String type, Value handler) throws ClassNotFoundException {
    registerNewEventHandler(Class.forName(type).asSubclass(Event.class), handler);
  }

  /**
   * Registers an event handler for the specific event type
   * 
   * @param type    Type
   * @param handler
   * @throws ClassNotFoundException
   */
  public static void registerNewEventHandler(Class<? extends Event> type, Value handler) throws ClassNotFoundException {
    Listener lis = new Listener() {
      public int hashCode() {
        return super.hashCode();
      }
    };
    EventExecutor exec = (listener, event) -> {
      try {
        handler.execute(event);
      } catch (Exception exception) {
        // do nothing
      }
    };

    Bukkit.getPluginManager().registerEvent(type, lis, EventPriority.NORMAL, exec, Cauldron.instance());
  }

  /**
   * Creates a command bound to Cauldron
   */
  public static Command createCommand(String name, final Value handler) {
    return new Command(name) {
      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return handler.execute(sender, commandLabel, args).asBoolean();
      }
    };
  }
}