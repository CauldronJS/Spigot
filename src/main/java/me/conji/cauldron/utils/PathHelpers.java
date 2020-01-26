package me.conji.cauldron.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.api.JsAccess;

@JsAccess.INNER_BINDING("path_helpers")
public class PathHelpers {
  public static String join(String path1, String... paths) {
    return Paths.get(path1, paths).toString();
  }

  public static boolean exists(String path1, String... paths) {
    return Files.exists(Paths.get(path1, paths));
  }

  public static boolean exists(File file, String... paths) {
    return exists(file.getAbsolutePath(), paths);
  }

  public static boolean exists(Path path, String... paths) {
    return exists(path.toFile(), paths);
  }

  public static Path resolveLocalPath(String path1, String... paths) {
    return Paths.get(Cauldron.instance().getDataFolder().toPath().resolve(path1).toString(), paths);
  }

  public static File resolveLocalFile(String path1, String... paths) {
    return Paths.get(Cauldron.instance().getDataFolder().toPath().resolve(path1).toString(), paths).toFile();
  }

  public static File resolveLocalFile(File file, String... paths) {
    return Paths.get(Cauldron.instance().getDataFolder().toPath().resolve(file.getPath()).toString(), paths).toFile();
  }

  public static boolean existsLocal(String path1, String... paths) {
    return resolveLocalFile(path1, paths).exists();
  }

  public static boolean existsLocal(File file, String... paths) {
    return resolveLocalFile(file, paths).exists();
  }

  public static boolean existsEmbedded(String path1, String... paths) {
    return Cauldron.instance().getResource(join(path1, paths)) != null;
  }

  public static BufferedReader readLocal(String path, String... paths) throws FileNotFoundException {
    File localFile = resolveLocalFile(path, paths);
    FileInputStream fis = new FileInputStream(localFile);
    return new BufferedReader(new InputStreamReader(fis));
  }

  public static BufferedReader readEmbedded(String name) {
    InputStream is = Cauldron.instance().getResource(name);
    return new BufferedReader(new InputStreamReader(is));
  }
}