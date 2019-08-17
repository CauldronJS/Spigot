package me.conji.cauldron;

import de.mxro.process.Spawn;
import me.conji.cauldron.factories.InjectionFactory;
import me.conji.cauldron.utils.PathHelper;
import me.conji.cauldron.utils.ScriptHelper;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class Cauldron extends JavaPlugin {
    static Cauldron instance;

    Context context;
    Reflections reflections;

    public Cauldron() {
        instance = this;
        // reflections = new Reflections("me.conji.cauldron");
    }

    public Context getContext() {
        return this.context;
    }

    public Reflections getReflections() {
        return this.reflections;
    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.createDataFolder();
        }
        this.bootstrap();
    }

    @Override
    public void onDisable() {
        try {
            this.context.close(true);
        } finally {}
    }

    private void createDataFolder() {
        this.getDataFolder().mkdirs();
        Path datadir = this.getDataFolder().toPath();
        try {
            Files.copy(
                    this.getResource("package.json"),
                    datadir.resolve("package.json")
            );
            datadir.resolve("src").toFile().mkdir();
            Files.copy(
                    this.getResource("src/index.js"),
                    datadir.resolve("src/index.js")
            );
        } catch (IOException ex) {
            this.getLogger().log(Level.WARNING, "Failed to generate data folders: " + ex.toString());
        }
    }

    private void bootstrap() {
        if (this.context != null) {
            this.context.close(true);
        }
        this.context = Context.newBuilder("js")
                .option("js.ecmascript-version", "10")
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowCreateThread(true)
                .allowHostClassLoading(true)
                .allowIO(true)
                .build();
        try {
            // InjectionFactory.initialize(this.reflections);
            this.evalScript(
                    ScriptHelper.readFile("lib/internal/bootstrap/loaders.js"),
                    "lib/internal/bootstrap/loaders.js"
            );
        } catch (IOException ex) {
            this.log(Level.SEVERE, "Failed to bootstrap: " + ex.toString());
        }
    }

    public String runProcess(String command, String dir) {
        return Spawn.sh(PathHelper.getFile(dir), command);
    }

    public Value evalScript(String contents, String filename) throws IOException {
        Source source = Source.newBuilder("js", contents, filename).build();
        return this.context.eval(source);
    }

    public String readFile(String path) throws IOException {
        return ScriptHelper.readFile(path);
    }

    public File getFile(String path) {
        return PathHelper.getFile(path);
    }

    public void registerNewEventHandler(String type, Value handler) throws ClassNotFoundException {
        Class clazz = Class.forName(type);
        Listener lis = new Listener() {
            public int hashCode() {
                return super.hashCode();
            }
        };
        EventExecutor exec = (listener, event) -> {
            try {
                handler.execute(new Object[] { event });
            } catch (Exception exception) {}
        };

        getServer().getPluginManager().registerEvent(clazz, lis, EventPriority.NORMAL, exec, this);
    }

    public Command createCommand(String name, final Value handler) {
        return new Command(name) {
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return handler.execute(sender, commandLabel, args).asBoolean();
            }
        };
    }

    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    public static Cauldron getInstance() {
        return instance;
    }
}
