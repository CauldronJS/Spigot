package me.conji.cauldron.core;

import java.io.InputStream;

import me.conji.cauldron.Cauldron;

public class FileReader {
  public static String read(String location) {
    Cauldron cauldron = Isolate.activeIsolate().cauldron();
    // first read from the disk "lib" dir, then read from resources
    // if neither exist, read from disk
  }
}