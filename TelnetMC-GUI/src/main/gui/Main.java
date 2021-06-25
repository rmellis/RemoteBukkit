/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.gui;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class Main {

	public static boolean doEncrypt;
    private static final File cache = new File("./", "remotebukkitgui.properties");
    private static String version;

    public static File getCacheFile() {
        return cache;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
    	doEncrypt = !cache.exists();
        try {
            Properties meta = new Properties();
            meta.load(Main.class.getResourceAsStream("/meta.properties"));

            version = meta.getProperty("version");
            
        } catch (NullPointerException ignored) {
        }

        if (version == null) {
            version = "X.X.X";
        }

        System.out.println("Launching TelnetMC");
        System.out.println();

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        StartDialog sd = new StartDialog();

        if (args.length > 0) {
            if (args.length == 1) {
                if (args[0].equals("--help")) {
                    printHelpAndExit(0);
                } else {
                    System.out.println("Incorrect Argument Syntax!");
                    printHelpAndExit(1);
                }
            } else if (args.length == 3) {
                String[] hostAndPort = args[0].split(":");

                if (hostAndPort.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
                    System.out.println("Incorrect Argument Syntax!");
                    printHelpAndExit(1);
                }

                try {
                    Integer.parseInt(hostAndPort[1]);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, hostAndPort[1] + " is not a valid 32-bit integer.", "Error", JOptionPane.ERROR_MESSAGE);

                    System.exit(1);
                }

                sd.getHost().setText(hostAndPort[0]);
                sd.getPort().setText(hostAndPort[1]);
                sd.getUsername().setText(args[1]);
                sd.getPassword().setText(args[2]);

                sd.setVisible(true);

                sd.connect();
            } else {
                System.out.println("Incorrect Argument Syntax!");
                printHelpAndExit(1);
            }
        } else {
            if (cache.exists()) {
                Properties p = new Properties();
                p.load(new FileReader(cache));
                
                sd.getHost().setText(p.getProperty("host", ""));
                sd.getPort().setText(p.getProperty("port", ""));
                sd.getUsername().setText(p.getProperty("username", ""));
                sd.getPassword().setText(p.getProperty("password", ""));
                if(sd.getHost().getText().length() > 1
                        && sd.getPort().getText().length() > 1
                        && sd.getUsername().getText().length() > 1
                        && sd.getPassword().getPassword().length > 1) {
                    sd.getRemember().setSelected(true);
                }
            }

            sd.setVisible(true);
        }
    }

    private static void printHelpAndExit(int exitCode) {
        System.out.println("Run the GUI with no arguments to open the Login Dialog.");
        System.out.println("Run the GUI with the following arguments and it will attempt to use the supplied parameters to login automatically:");
        System.out.println();
        System.out.println("Use: [hostname:ip] [user] [pass] <switches>");
        System.out.println();
        System.out.println("Switches:");
        System.out.println("--help       Prints this help message.");

        System.exit(exitCode);
    }
    public static String SHA512(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashtext = new StringBuilder(no.toString(16));

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            // return the HashText
            return hashtext.toString();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
