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
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class SocketForwardThread extends Thread {

    private final GUI gui;
    private final BufferedReader in;

    public SocketForwardThread(GUI gui, InputStream in) {
        super("Socket Forward Thread");

        this.gui = gui;
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = in.readLine();

                if (input == null) {
                    String[] lines = gui.getConsole().getText().split("\n");

                    System.out.println(lines[lines.length - 3]);
                    System.out.println(lines[lines.length - 1]);

                    if (lines[lines.length - 3].equals("RemoteBukkit closing connection for reason:\r")) {
                        JOptionPane.showMessageDialog(gui, "Server closed connection for reason:\n\n" + lines[lines.length - 1], "Error", JOptionPane.ERROR_MESSAGE);

                        System.exit(0);
                    }

                    JOptionPane.showMessageDialog(gui, "Server closed connection.", "Error", JOptionPane.ERROR_MESSAGE);

                    System.exit(0);
                } else if (input.equals("Incorrect Credentials.")) {
                    JOptionPane.showMessageDialog(gui, "Incorrect Credentials.", "Error", JOptionPane.ERROR_MESSAGE);

                    System.exit(0);
                }

                gui.getConsole().appendANSI(input + "\n");
            }
        } catch (IOException | BadLocationException ex) {
            JOptionPane.showMessageDialog(gui, "Connection to server lost:\n\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }
}
