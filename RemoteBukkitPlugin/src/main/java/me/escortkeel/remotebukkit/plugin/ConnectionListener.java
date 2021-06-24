/*
 * Copyright (c) 2013, Keeley Hoek
 * All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * 
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
