package me.conji.cauldron.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.sun.istack.internal.NotNull;
import me.conji.cauldron.api.async.AsyncContext;
import org.graalvm.polyglot.Value;

public interface CauldronApi {
  public Path getPath();

  public CauldronVM getVM();

  public AsyncContext getAsyncContext();

  public ProcessRunner getProcessRunner();

  public InputStream getResource(@NotNull String name);

  public LanguageEngine getEngine();

  public Logger getLogger();
}