/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
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
