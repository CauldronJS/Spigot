package me.conji.cauldron.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import com.google.common.util.concurrent.ListenableFuture;

import org.bukkit.plugin.Plugin;

import me.conji.cauldron.api.JsAccess;

@JsAccess.INNER_BINDING("file_reader")
public class FileReader {
  public static File getFile(Plugin cauldron, String location) {
    String localizedName = Paths.get(cauldron.getDataFolder().getPath(), location).toString();
    File localFile = new File(localizedName);
    return localFile;
  }

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

  public static void write(Plugin cauldron, String location, String content, int position, String encoding) {
    File localizedFile = Paths.get(cauldron.getDataFolder().getPath(), location).toFile();
    try {
      localizedFile.createNewFile();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localizedFile)));
      writer.write(content, position, content.length());
      writer.flush();
      writer.close();
    } catch (Exception ex) {
      cauldron.getLogger().log(Level.SEVERE, ex.toString());
    }
  }
}