/*
 * Copyright (c) 2012, Keeley Hoek
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
package me.escortkeel.remotebukkit.gui;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class ServerConnectionThread extends Thread {

    private final StartDialog sd;

    public ServerConnectionThread(StartDialog sd) {
        this.sd = sd;
    }

    @Override
    public void run() {
        try {
            sd.getProg().setString("Resolving Hostname and Binding to Server Port");
            sd.getProg().setValue(0);

            final Socket s = new Socket(sd.getHost().getText(), Integer.parseInt(sd.getPort().getText()));
            
            sd.getProg().setString("Registering Shutdown Hooks");
            sd.getProg().setValue(25);

            Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Thread") {

                @Override
                public void run() {
                    try {
                        s.close();
                    } catch (IOException ignored) {
                    }
                }
            });

            sd.getProg().setString("Starting Connection Handler");
            sd.getProg().setValue(50);

            PrintStream out = new PrintStream(s.getOutputStream());

            GUI gui = new GUI(out);

            SocketForwardThread ift = new SocketForwardThread(gui, s.getInputStream());
            ift.setDaemon(true);
            ift.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    s.close();
                } catch (IOException ignored) {
                }
            }));

            sd.getProg().setString("Authenticating");
            sd.getProg().setValue(75);

            out.println(sd.getUsername().getText());
            out.println(new String(sd.getPassword().getPassword()));
            out.println(Directive.INTERACTIVE); //TODO Selectable direcrive

            sd.getProg().setString("Done!");
            sd.getProg().setValue(100);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            sd.dispose();

            gui.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(sd, "Failed to connect to server:\n\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }
}
