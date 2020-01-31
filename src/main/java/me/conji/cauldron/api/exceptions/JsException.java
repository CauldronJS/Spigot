package me.conji.cauldron.api.exceptions;

import java.util.stream.Stream;

import me.conji.cauldron.Cauldron;

public class JsException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 1599937854838882128L;

  public JsException(Throwable throwable) {
    super("[" + throwable.getClass().getName() + "]: " + throwable.getMessage());
    boolean isDebugging = Cauldron.instance().getIsDebugging();
    Stream<StackTraceElement> stackTrace = Stream.of(throwable.getStackTrace());
    StackTraceElement[] cleanedTrace = stackTrace.filter(stackTraceElement -> {
      return isDebugging ? true : !stackTraceElement.toString().contains("lib/internal/modules/loader.js");
    }).toArray(StackTraceElement[]::new);
    this.setStackTrace(cleanedTrace);
  }
}