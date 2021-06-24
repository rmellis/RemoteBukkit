/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.plugin;
/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
public enum Directive {

    INTERACTIVE(""),
    NOLOG("NOLOG");

    public static Directive toDirective(String raw) {
        for (Directive d : Directive.values()) {
            if (d.toString().equalsIgnoreCase(raw)) {
                return d;
            }
        }

        return null;
    }
    public final String qualifier;

    Directive(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        return qualifier;
    }
}
