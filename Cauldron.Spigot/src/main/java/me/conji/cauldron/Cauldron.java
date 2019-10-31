package me.conji.cauldron;

import me.conji.cauldron.api.CauldronApi;
import me.conji.cauldron.api.CauldronVM;
import me.conji.cauldron.api.LanguageEngine;
import me.conji.cauldron.api.ProcessRunner;
import me.conji.cauldron.api.async.AsyncContext;
import me.conji.cauldron.api.js.JavaScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.*;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;

public class Cauldron extends JavaPlugin implements CauldronApi {
    private static Cauldron instance;

    private Reflections reflections;
    private CauldronVM vm;
    private JavaScriptEngine engine;
    private SpigotAsyncContext asyncContext;

    @Override
    public void onEnable() {
        instance = this;
        this.reflections = new Reflections("me.conji.cauldron");
        this.engine = new JavaScriptEngine(this);
        this.vm = new CauldronVM(this.engine);

        try {
            this.engine.initialize(this.getDataFolder());
            this.asyncContext = new SpigotAsyncContext();
            InputStream loader = this.engine.getFile("lib/internal/bootstrap/loaders.js");
            this.engine.eval(loader, "lib/internal/bootstrap/loaders.js");

        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Failed to bootstrap Cauldron: " + ex.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // dispose of engine and wait for all promises to finish
    }

    public static void registerNewEventHandler(String type, Value handler) throws ClassNotFoundException {
        Class clazz = Class.forName(type);
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

        Bukkit.getPluginManager().registerEvent(clazz, lis, EventPriority.NORMAL, exec, instance);
    }

    public static Command createCommand(String name, final Value handler) {
        return new Command(name) {
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return handler.execute(sender, commandLabel, args).asBoolean();
            }
        };
    }

    @Override
    public Path getPath() {
        return this.getDataFolder().toPath();
    }

    @Override
    public CauldronVM getVM() {
        return this.vm;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return this.asyncContext;
    }

    @Override
    public ProcessRunner getProcessRunner() {
        return null;
    }

    @Override
    public LanguageEngine getEngine() {
        return this.engine;
    }

    public Reflections getReflections() {
        return reflections;
    }
}
