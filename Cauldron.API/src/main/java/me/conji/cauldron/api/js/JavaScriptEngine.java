package me.conji.cauldron.api.js;

import me.conji.cauldron.api.CauldronApi;
import me.conji.cauldron.api.LanguageEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class JavaScriptEngine implements LanguageEngine {
  private Context context;
  private CauldronApi api;
  private File processDir;
  private boolean isInitialized = false;

  public JavaScriptEngine(CauldronApi api) {
    this.api = api;
  }

  @Override
  public Value eval(String contents, String filename) throws IOException {
    Source source = Source.newBuilder("js", contents, filename).build();
    return this.context.eval(source);
  }

  @Override
  public Value eval(InputStream contentStream, String filename) throws IOException {
    InputStreamReader isr = new InputStreamReader(contentStream);
    BufferedReader br = new BufferedReader(isr);
    StringBuilder result = new StringBuilder();
    String currentLine;
    while((currentLine = br.readLine()) != null) {
      result.append(currentLine);
    }
    isr.close();
    br.close();
    return this.eval(result.toString(), filename);
  }

  @Override
  public void put(String identifier, Object value) {
    this.context.getPolyglotBindings().putMember(identifier, value);
  }

  @Override
  public void initialize(File directory) throws IOException {
    this.processDir = directory;
    this.context = Context.newBuilder("js")
      .option("js.ecmascript-version", "10")
      .allowHostAccess(HostAccess.ALL)
      .allowCreateThread(false)
      .allowHostClassLoading(true)
      .allowIO(false)
      .build();
    this.api.getLogger().log(Level.INFO, "Initialized JS engine");
    if (!directory.exists()) {
      directory.mkdirs();
      // initialize from empty directory
      Path dirPath = directory.toPath();
      dirPath.resolve("src").toFile().mkdir();
      Files.copy(this.api.getResource("package.json"), dirPath.resolve("package.json"));
      Files.copy(this.api.getResource("src/index.js"), dirPath.resolve("src/index.js"));

      // override native types with Cauldron types
      this.put("Promise", CauldronPromise.class);
    }
    this.isInitialized = true;
  }

  @Override
  public InputStream getFile(String path) {
    try {
      return new FileInputStream(this.processDir.toPath().resolve(path).toFile());
    } catch (FileNotFoundException fnfex) {
      InputStream resxStream = this.api.getResource(path);
      try {
        // just to see if there's a resource there or not
        resxStream.available();
        return resxStream;
      } catch (IOException ioex) {
        return null;
      }
    }
  }

  @Override
  public boolean isInitialized() {
    return this.isInitialized;
  }
}
