package me.escortkeel.remotebukkit.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ConnectionHandler extends Thread {
  private final RemoteBukkitPlugin plugin;
  
  private final int number;
  
  private final Socket socket;
  
  private final PrintStream out;
  
  private Directive directive;
  
  private volatile boolean killed = false;
  
  public ConnectionHandler(RemoteBukkitPlugin plugin, int number, Socket socket) throws IOException {
    super("RemoteBukkit-ConnectionHandler");
    setDaemon(true);
    this.plugin = plugin;
    this.number = number;
    this.socket = socket;
    this.out = new PrintStream(socket.getOutputStream());
  }
  
  public void run() {
    RemoteBukkitPlugin.log("Connection #" + this.number + " from " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " was accepted.");
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      String user = in.readLine();
      String pass = in.readLine();
      if (user == null || pass == null)
        throw new IOException("Connection terminated before all credentials could be sent!"); 
      if (this.plugin.areValidCredentials(user, pass)) {
        String raw = in.readLine();
        if (raw == null)
          throw new IOException("Connection terminated before connection directive could be recieved!"); 
        this.directive = Directive.toDirective(raw);
        if (this.directive == null) {
          RemoteBukkitPlugin.log("Connection #" + this.number + " from " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " requested the use of an unsupported directive (\"" + raw + "\").");
          kill("Unsported directive \"" + raw + "\".");
        } else {
          this.plugin.didEstablishConnection(this, this.directive);
          while (true) {
            final String input = in.readLine();
            if (input == null)
              break; 
            if (this.plugin.doVerboseLogging())
              RemoteBukkitPlugin.log("Connection #" + this.number + " from " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " dispatched command: " + input); 
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, new Runnable() {
                  public void run() {
                    ConnectionHandler.this.plugin.getServer().dispatchCommand((CommandSender)ConnectionHandler.this.plugin.getServer().getConsoleSender(), input);
                  }
                });
          } 
        } 
      } else {
        RemoteBukkitPlugin.log("Connection #" + this.number + " from " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " attempted to authenticate using incorrect credentials.");
        kill("Incorrect credentials.");
      } 
    } catch (IOException ex) {
      RemoteBukkitPlugin.log("Connection #" + this.number + " from " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " abruptly closed the connection during authentication.");
    } 
    kill();
  }
  
  public int getNumber() {
    return this.number;
  }
  
  public Socket getSocket() {
    return this.socket;
  }
  
  public void kill() {
    if (this.killed)
      return; 
    this.killed = true;
    this.plugin.didCloseConnection(this);
    try {
      this.socket.close();
    } catch (IOException iOException) {}
  }
  
  public void kill(String reason) {
    this.directive = Directive.INTERACTIVE;
    send("\nTelnetMC closing connection because:");
    send(reason);
    kill();
  }
  
  public void send(String msg) {
    if (this.directive != Directive.NOLOG)
      this.out.println(msg); 
  }
}
