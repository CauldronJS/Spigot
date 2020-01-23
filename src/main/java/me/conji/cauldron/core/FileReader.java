package me.conji.cauldron.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.conji.cauldron.utils.PathHelpers;

public class FileReader {
  public static String read(String location) throws FileNotFoundException, IOException {
    // first read from the disk "lib" dir, then read from resources
    // if neither exist, read from disk
    // god I hate all these variables and I'm THIS close to making a helper file for
    // this shit
    BufferedReader reader = null;
    if (PathHelpers.existsLocal(location)) {
      try {
        reader = PathHelpers.readLocal(location);
      } catch (FileNotFoundException ex) {
        // ignore because we check
      }
    } else if (PathHelpers.existsEmbedded(location)) {
      reader = PathHelpers.readEmbedded(location);
    } else {
      throw new FileNotFoundException(location);
    }

    String result = "";
    String line;
    while ((line = reader.readLine()) != null) {
      result += (line + System.lineSeparator());
    }

    return result;
  }
}