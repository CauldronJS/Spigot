package me.conji.cauldron.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.plugin.Plugin;

import me.conji.cauldron.utils.PathHelpers;
import me.conji.cauldron.Cauldron;
import me.conji.cauldron.api.JsAccess;

@JsAccess.INNER_BINDING("file_reader")
public class FileReader {
  public static String read(Plugin cauldron, String location) throws FileNotFoundException, IOException {
    // first read from the disk "lib" dir, then read from resources
    // if neither exist, read from disk
    // god I hate all these variables and I'm THIS close to making a helper file for
    // this shit
    String localizedName = Paths.get(cauldron.getDataFolder().getPath(), location).toString();
    BufferedReader reader = null;
    File localFile = new File(localizedName);
    if (localFile.exists()) {
      try {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(localizedName)));
      } catch (FileNotFoundException ex) {
        // skip because it should be found
      }
    } else if (cauldron.getResource(location) != null) {
      reader = new BufferedReader(new InputStreamReader(cauldron.getResource(location)));
    } else {
      throw new FileNotFoundException(localizedName);
    }

    String result = "";
    String line;
    while ((line = reader.readLine()) != null) {
      result += (line + System.lineSeparator());
    }

    return result;
  }
}