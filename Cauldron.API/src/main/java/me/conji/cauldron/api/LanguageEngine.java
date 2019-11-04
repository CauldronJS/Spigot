package me.conji.cauldron.api;

import com.sun.istack.internal.NotNull;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.*;

public interface LanguageEngine {
  public Value bindings();
  public Value eval(@NotNull Source source, String filename) throws IOException;
  public Value eval(@NotNull String contents, @NotNull String filename) throws IOException;
  public Value eval(@NotNull InputStream contentStream, @NotNull String filename) throws IOException;
  public Value get(@NotNull String identifier);
  public void put(@NotNull String identifier, Object value);
  public void initialize(File directory) throws IOException;
  public InputStream getFile(String path);
  public boolean isInitialized();
}
