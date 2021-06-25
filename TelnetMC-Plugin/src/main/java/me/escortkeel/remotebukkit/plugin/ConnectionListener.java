/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.plugin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
public class ConnectionListener extends Thread {

    private final RemoteBukkitPlugin plugin;
    private final ServerSocket s;
    private int number = 0;

    public ConnectionListener(RemoteBukkitPlugin plugin, int port) {
        super("RemoteBukkit-ConnectionListener");
        this.setDaemon(true);

        this.plugin = plugin;

        try {
            s = new ServerSocket(port);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to listen on port:" + port, ex);
        }
    }

    @Override
    public void run() {
        while (!s.isClosed()) {
            Socket socket = null;
            try {
                socket = s.accept();

                ConnectionHandler con = new ConnectionHandler(plugin, number++, socket);
                con.start();
            } catch (IOException ex) {
                if (socket != null) {
                    RemoteBukkitPlugin.log("Error while trying to accept connection #" + (number - 1) + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), ex);
                }
            }
        }
    }

    public void kill() {
        try {
            s.close();
        } catch (IOException ignored) {
        }
    }
}
