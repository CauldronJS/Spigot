package me.conji.cauldron.utils;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.builders.PathBuilder;
import org.graalvm.polyglot.HostAccess;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PathHelper {
    @HostAccess.Export
    public static String getPath(String path) {
        return new PathBuilder().add(path).toString();
    }

    public static String getCauldronPath() {
        return Cauldron.getInstance().getDataFolder().getAbsolutePath();
    }

    @HostAccess.Export
    public static File getFile(String path) {
        return new File(getPath(path));
    }
}
