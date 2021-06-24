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
public class User {
    private final String username;
    private final String password;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;        
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
