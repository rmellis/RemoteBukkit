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
package me.escortkeel.remotebukkit.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
/**
 * @author Keeley Hoek (escortkeel@gmail.com)
 * @update rmellis - TelnetMC fork
 **/
public class Main {

    private static boolean nolog = false;
    private static String exec = null;

    public static void main(String[] args) throws IOException {
        String version = null;

        try {
            Properties meta = new Properties();
            meta.load(Main.class.getResourceAsStream("/meta.properties"));

            version = meta.getProperty("version");
        } catch (NullPointerException ex) {
        }

        if (version == null) {
            version = "X.X.X";
        }

        System.out.println("Launching TelnetMC console client..");
        System.out.println("Please wait..");
        System.out.println("Done!");
        System.out.println();

        try {
            if (args.length >= 3 && checkSwitches(Arrays.copyOfRange(args, 3, args.length))) {
                Scanner sc = new Scanner(args[0]).useDelimiter(":");
                try (Socket s = new Socket(sc.next(), sc.nextInt())) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final PrintStream out = new PrintStream(s.getOutputStream());

                    out.println(args[1]);
                    out.println(args[2]);

                    if (nolog) {
                        out.println(Directive.NOLOG);
                    }

                    if (exec != null) {
                        System.out.println("Executing command: " + exec);

                        if ("".equals(in.readLine())) {
                            System.out.println();
                            System.out.println("Error while attempting to connect to server.");
                            System.out.println("The error message can be found below.");
                            System.out.println();
                            System.out.println(in.readLine());
                            System.out.println(in.readLine());

                            System.exit(1);
                        }

                        out.println(exec);
                    } else {
                        if (!nolog) {
                            out.println(Directive.INTERACTIVE);
                        }

                        final ConsoleReader console = new ConsoleReader();

                        Thread ift = new Thread("Input Forward Thread") {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        out.println(console.readLine(">"));
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        };
                        ift.setDaemon(true);
                        ift.start();

                        while (true) {
                            String msg = in.readLine();

                            if (msg == null) {
                                break;
                            } else {
                                console.println('\r' + msg);
                                console.flush();
                                console.drawLine();
                                console.flush();
                            }
                        }
                    }
                }
            } else {
                System.out.println("Invalid argument syntax!");
                System.out.println();
                printHelpAndExit(1);
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Invalid hostname-port pair!");
            System.out.println();
            printHelpAndExit(1);
        }
    }

    private static boolean checkSwitches(String[] switches) {
        for (int i = 0; i < switches.length; i++) {
            if (!switches[i].startsWith("--")) {
                return false;
            }

            String arg = switches[i].substring(2);

            switch (arg) {
                case "help":
                    printHelpAndExit(0);
                    break;
                case "exec":
                    if (i + 1 < switches.length) {
                        exec = switches[i + 1];
                        i++;
                    } else {
                        System.out.println("The --exec switch requires a parameter.");
                        return false;
                    }
                //Intentional follow-on
                case "nolog":
                    nolog = true;
                    break;
                default:
                    System.out.println("Invalid switch: " + arg);
                    return false;
            }
        }

        return true;
    }

    private static void printHelpAndExit(int exitCode) {
        System.out.println("Use: [address:port] [user] [pass] <switches>");
        System.out.println();
        System.out.println("Switches:");
        System.out.println("--help           Prints this help message.");
        System.out.println("--nolog          Instructs the server not to send any log information to the client.");
        System.out.println("--exec <command> Sends <command> and then exits. Implies --nolog.");

        System.exit(exitCode);
    }
}
