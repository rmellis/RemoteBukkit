package me.escortkeel.remotebukkit.plugin;

public enum Directive {
  INTERACTIVE(""),
  NOLOG("NOLOG");
  
  public final String qualifier;
  
  public static Directive toDirective(String raw) {
    for (Directive d : values()) {
      if (d.toString().equalsIgnoreCase(raw))
        return d; 
    } 
    return null;
  }
  
  Directive(String qualifier) {
    this.qualifier = qualifier;
  }
  
  public String toString() {
    return this.qualifier;
  }
}
