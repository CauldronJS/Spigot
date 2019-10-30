package me.conji.cauldron.api;

import java.io.InputStream;
import java.nio.file.Path;

import com.sun.istack.internal.NotNull;
import me.conji.cauldron.api.async.AsyncContext;

public interface CauldronApi {
  public Path getPath();

  public AsyncContext getAsyncContext();

  public ProcessRunner getProcessRunner();

  public InputStream getResource(@NotNull String name);
}