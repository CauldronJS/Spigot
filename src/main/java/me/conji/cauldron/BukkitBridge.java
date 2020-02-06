package me.conji.cauldron;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.HostAccess;

public class BukkitBridge {
  public static void registerNewEventHandler(Plugin cauldron, String type, Value handler)
      throws ClassNotFoundException {
    registerNewEventHandler(cauldron, Class.forName(type).asSubclass(Event.class), handler);
  }

  /**
   * Registers an event handler for the specific event type
   * 
   * @param type    Type
   * @param handler
   * @throws ClassNotFoundException
   */
  @HostAccess.Export
  public static void registerNewEventHandler(Plugin cauldron, Class<? extends Event> type, Value handler)
      throws ClassNotFoundException {
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

    Bukkit.getPluginManager().registerEvent(type, lis, EventPriority.NORMAL, exec, cauldron);
  }

  /**
   * Creates a command bound to Cauldron
   */
  @HostAccess.Export
  public static Command createCommand(String name, final Value handler) {
    return new Command(name) {
      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return handler.execute(sender, commandLabel, args).asBoolean();
      }
    };
  }
}