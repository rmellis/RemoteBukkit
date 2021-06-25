/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
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

    @Override
    public void run() {
        RemoteBukkitPlugin.log("Connection #" + number + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " was accepted.");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String user = in.readLine();
            String pass = in.readLine();

            if (user == null || pass == null) {
                throw new IOException("Connection terminated before all credentials could be sent!");
            }

            if (plugin.areValidCredentials(user, pass)) {
                String raw = in.readLine();

                if (raw == null) {
                    throw new IOException("Connection terminated before connection directive could be recieved!");
                }

                directive = Directive.toDirective(raw);
                if (directive == null) {
                    RemoteBukkitPlugin.log("Connection #" + number + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " requested the use of an unsupported directive (\"" + raw + "\").");
                    kill("Unsupported directive \"" + raw + "\".");
                } else {
                    plugin.didEstablishConnection(this, directive);

                    while (true) {
                        final String input = in.readLine();

                        if (input == null) {
                            break;
                        }

                        if (plugin.doVerboseLogging()) {
                            RemoteBukkitPlugin.log("Connection #" + number + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " dispatched command: " + input);
                        }

                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), input);
                            }
                        });
                    }
                }
            } else {
                RemoteBukkitPlugin.log("Connection #" + number + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " attempted to authenticate using incorrect credentials.");
                kill("Incorrect credentials.");
            }
        } catch (IOException ex) {
            RemoteBukkitPlugin.log("Connection #" + number + " from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " abruptly closed the connection during authentication.");
        }

        kill();
    }

    public int getNumber() {
        return number;
    }

    public Socket getSocket() {
        return socket;
    }

    public void kill() {
        if (killed) {
            return;
        }
        killed = true;

        plugin.didCloseConnection(this);

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public void kill(String reason) {
        directive = Directive.INTERACTIVE;

        send("\nTelnetMC closing connection because:");
        send(reason);

        kill();
    }

    public void send(String msg) {
        if (Directive.NOLOG != directive) {
            out.println(msg);
        }
    }
}
