package me.conji.cauldron.builders;

import me.conji.cauldron.Cauldron;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathBuilder {
    private ArrayList<String> paths = new ArrayList<>();

    public PathBuilder add(String path) {
        paths.add(path);
        return this;
    }

    @Override
    public String toString() {
        Path path = Paths.get(Cauldron.getInstance().getDataFolder().getAbsolutePath(), paths.toArray(new String[0]));
        return path.normalize().toString();
    }
}
