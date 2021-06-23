package me.escortkeel.remotebukkit.plugin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread {
  private final RemoteBukkitPlugin plugin;
  
  private final ServerSocket s;
  
  private int number = 0;
  
  public ConnectionListener(RemoteBukkitPlugin plugin, int port) {
    super("RemoteBukkit-ConnectionListener");
    setDaemon(true);
    this.plugin = plugin;
    try {
      this.s = new ServerSocket(port);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to listen on port:" + port, ex);
    } 
  }
  
  public void run() {
    while (!this.s.isClosed()) {
      Socket socket = null;
      try {
        socket = this.s.accept();
        ConnectionHandler con = new ConnectionHandler(this.plugin, this.number++, socket);
        con.start();
      } catch (IOException ex) {
        if (socket != null)
          RemoteBukkitPlugin.log("Exception while attempting to accept connection #" + (this.number - 1) + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), ex); 
      } 
    } 
  }
  
  public void kill() {
    try {
      this.s.close();
    } catch (IOException iOException) {}
  }
}
