package me.escortkeel.remotebukkit.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class RemoteBukkitPlugin extends JavaPlugin {
  private static final Logger log = Logger.getLogger("Minecraft-Server");
  
  private static final Logger logger = (Logger)LogManager.getRootLogger();
  
  private static final ArrayList<String> oldMsgs = new ArrayList<>();
  
  private boolean verbose;
  
  public static void log(String msg) {
    log.log(Level.INFO, "[TelnetMC] " + msg);
  }
  
  public static void log(String msg, IOException ex) {
    log.log(Level.INFO, "[TelnetMC] " + msg, ex);
  }
  
  private final ArrayList<ConnectionHandler> connections = new ArrayList<>();
  
  private ArrayList<User> users = new ArrayList<>();
  
  private LogAppender appender;
  
  private ConnectionListener listener;
  
  private int logsize;
  
  public void onLoad() {
    getConfig().options().copyDefaults(true);
  }
  
  public void onEnable() {
    this.appender = new LogAppender(this);
    log.log(Level.INFO, getDescription().getFullName().concat(" is enabled!"));
    logger.addAppender((Appender)this.appender);
    int port = 25564;
    try {
      int num = 0;
      List<Map<String, Object>> usersSection = null;
      try {
        usersSection = getConfig().getList("users");
      } catch (Exception exception) {}
      if (usersSection != null) {
        for (Map<String, Object> entry : usersSection) {
          num++;
          try {
            String username, password;
            Object rawUsername = entry.get("user");
            if (rawUsername instanceof String) {
              username = (String)rawUsername;
            } else if (rawUsername instanceof Integer) {
              username = ((Integer)rawUsername).toString();
            } else {
              log.log(Level.WARNING, "[TelnetMC] Invalid or no username specified for entry #" + num + ", defaulting to \"username\"");
              continue;
            } 
            Object rawPassword = entry.get("pass");
            if (rawPassword instanceof String) {
              password = (String)rawPassword;
            } else if (rawPassword instanceof Integer) {
              password = ((Integer)rawPassword).toString();
            } else {
              log.log(Level.WARNING, "[TelnetMC] Invalid or no password specified for entry #" + num + ", defaulting to \"password\"");
              continue;
            } 
            this.users.add(new User(username, password));
          } catch (Exception e) {
            String username;
            log.log(Level.WARNING, "[TelnetMC] Could not parse user entry #" + num + ", ignoring it (this entry will be deleted).");
          } 
        } 
      } else {
        System.out.println("NULLL! OMGPOY!");
      } 
      if (this.users.isEmpty()) {
        log.log(Level.WARNING, "[TelnetMC] No entries were provided. A default entry has been added (username = \"username\", password = \"password\").");
        this.users.add(new User("username", "password"));
      } 
      port = getConfig().getInt("port");
      if (port <= 1024) {
        log.log(Level.WARNING, "[TelnetMC] The port requested is ether already in use or you did not specify one, you must choose a port greater than 1024, for now we will use the default port 25500");
        port = 25564;
      } 
      this.verbose = getConfig().getBoolean("verbose");
      Object logsizeRaw = getConfig().get("logsize");
      if (logsizeRaw instanceof Integer) {
        this.logsize = ((Integer)logsizeRaw).intValue();
      } else {
        log.log(Level.WARNING, "[TelnetMC] Invalid or no maximum logsize specified (must be greater than or equal to 0), defaulting to \"500\"");
        this.logsize = 500;
      } 
    } catch (Exception ex) {
      log.log(Level.SEVERE, "[TelnetMC] There was an error while reading the configuration file. The defaults have been assumed. Please report this to rmellis", ex);
      this.users.clear();
      this.users.add(new User("username", "password"));
      this.logsize = 500;
    } 
    this.listener = new ConnectionListener(this, port);
    this.listener.start();
    saveConfig();
  }
  
  public void onDisable() {
    logger.removeAppender((Appender)this.appender);
    this.listener.kill();
    for (ConnectionHandler con : new ArrayList(this.connections))
      con.kill("Plugin is being disabled!"); 
  }
  
  public void broadcast(String msg) {
    synchronized (oldMsgs) {
      oldMsgs.add(msg);
      if (oldMsgs.size() > this.logsize)
        oldMsgs.remove((this.logsize == 0) ? 0 : 1); 
    } 
    for (ConnectionHandler con : new ArrayList(this.connections))
      con.send(msg); 
  }
  
  public void didEstablishConnection(ConnectionHandler con, Directive directive) {
    log("Connection #" + con.getNumber() + " from " + con.getSocket().getInetAddress().getHostAddress() + ":" + con.getSocket().getPort() + " was successfully established.");
    this.connections.add(con);
    if (directive == Directive.NOLOG) {
      con.send("Connection successfully established.");
    } else {
      synchronized (oldMsgs) {
        for (String msg : oldMsgs)
          con.send(msg); 
      } 
    } 
  }
  
  public void didCloseConnection(ConnectionHandler con) {
    log("Connection #" + con.getNumber() + " from " + con.getSocket().getInetAddress().getHostAddress() + ":" + con.getSocket().getPort() + " was closed.");
    this.connections.remove(con);
  }
  
  public boolean areValidCredentials(String username, String password) {
    for (User user : this.users) {
      if (user.getUsername().equals(username) && user.getPassword().equals(password))
        return true; 
    } 
    return false;
  }
  
  public boolean doVerboseLogging() {
    return this.verbose;
  }
}
