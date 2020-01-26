package me.conji.cauldron.api;

public class JsAccess {
  public static enum AccessType {
    STATIC, INSTANCE
  }

  public @interface BINDING {
    public String value();

    public AccessType access() default AccessType.STATIC;
  }

  public @interface INNER_BINDING {
    public String value();

    public AccessType access() default AccessType.STATIC;
  }

  public @interface GLOBAL {
    public String value();

    public AccessType access() default AccessType.STATIC;
  }
}