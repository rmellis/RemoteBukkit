/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.plugin;

import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
public class RemoteBukkitPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft-Server");
    private static final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
    private static final ArrayList<String> oldMsgs = new ArrayList<>();
    private boolean verbose;

    public static void log(String msg) {
        log.log(Level.INFO, "[TelnetMC] " + msg);
    }

    public static void log(String msg, IOException ex) {
        log.log(Level.INFO, "[TelnetMC] " + msg, ex);
    }
    private final ArrayList<ConnectionHandler> connections = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();
    private LogAppender appender;
    private ConnectionListener listener;
    private int logsize;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
    }

    @Override
    public void onEnable() {
        appender = new LogAppender(this);

        log.log(Level.INFO, getDescription().getFullName().concat(" is enabled!"));
        logger.addAppender(appender);

        int port = 25500;
        try {
            int num = 0;
            List<Map<String, Object>> usersSection = null;
            try {
                usersSection = Collections.unmodifiableList((List<Map<String, Object>>) getConfig().getList("users"));
            } catch (Exception ignored) {
            }
            if (usersSection == null) {
                System.out.println("NULLL!");
            } else {
                for (Map<String, Object> entry : usersSection) {
                    num++;
                    try {
                        String username, password;

                        Object rawUsername = entry.get("user");
                        if (rawUsername instanceof String) {
                            username = ((String) rawUsername);
                        } else if (rawUsername instanceof Integer) {
                            username = ((Integer) rawUsername).toString();
                        } else {
                            log.log(Level.WARNING, "[TelnetMC] Invalid or no username specified for entry #" + num + ", defaulting to \"username\"");
                            continue;
                        }

                        Object rawPassword = entry.get("pass");
                        if (rawPassword instanceof String) {
                            password = ((String) rawPassword);
                        } else if (rawPassword instanceof Integer) {
                            password = ((Integer) rawPassword).toString();
                        } else {
                            log.log(Level.WARNING, "[TelnetMC] Invalid or no password specified for entry #" + num + ", defaulting to \"password\"");
                            continue;
                        }

                        users.add(new User(username, password));
                    } catch (Exception e) {
                        log.log(Level.WARNING, "[TelnetMC] Could not parse user entry #" + num + ", ignoring it (this entry will be deleted).");
                    }
                }
            }

            if (users.isEmpty()) {
                log.log(Level.WARNING, "[TelnetMC] No entries were provided. A default entry has been added (username = \"username\", password = \"password\").");
                users.add(new User("username", "password"));
            }

            port = getConfig().getInt("port");
            if (port <= 1024) {
                log.log(Level.WARNING, "[TelnetMC] The port requested is ether already in use or you did not specify one, you must choose a port greater than 1024, for now we will use the default port 25500");

                port = 25500;
            }

            verbose = getConfig().getBoolean("verbose");

            Object logsizeRaw = getConfig().get("logsize");
            if (logsizeRaw instanceof Integer) {
                logsize = (Integer) logsizeRaw;
            } else {
                log.log(Level.WARNING, "[TelnetMC] Invalid or no maximum logsize specified (must be greater than or equal to 0), defaulting to \"500\"");

                logsize = 500;
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "[TelnetMC] There was an error while reading the configuration file. The defaults have been assumed. Please report this to rmellis", ex);

            users.clear();
            users.add(new User("username", "password"));
            logsize = 500;
        }
        listener = new ConnectionListener(this, port);

        listener.start();

        saveConfig();
    }

    @Override
    public void onDisable() {
        logger.removeAppender(appender);

        listener.kill();

        for (ConnectionHandler con : new ArrayList<>(connections)) {
            con.kill("Plugin is being disabled!");
        }
    }

    public void broadcast(String msg) {
        synchronized (oldMsgs) {
            oldMsgs.add(msg);
            if (oldMsgs.size() > logsize) {
                oldMsgs.remove(logsize == 0 ? 0 : 1);
            }
        }

        for (ConnectionHandler con : new ArrayList<>(connections)) {
            con.send(msg);
        }
    }

    public void didEstablishConnection(ConnectionHandler con, Directive directive) {
        RemoteBukkitPlugin.log("Connection #" + con.getNumber() + " from " + con.getSocket().getInetAddress().getHostAddress() + ":" + con.getSocket().getPort() + " was successfully established.");

        connections.add(con);

        if (directive == Directive.NOLOG) {
            con.send("Connection successfully established.");
        } else {
            synchronized (oldMsgs) {
                for (String msg : oldMsgs) {
                    con.send(msg);
                }
            }
        }
    }

    public void didCloseConnection(ConnectionHandler con) {
        RemoteBukkitPlugin.log("Connection #" + con.getNumber() + " from " + con.getSocket().getInetAddress().getHostAddress() + ":" + con.getSocket().getPort() + " was closed.");

        connections.remove(con);
    }

    public boolean areValidCredentials(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    public boolean doVerboseLogging() {
        return verbose;
    }
}
