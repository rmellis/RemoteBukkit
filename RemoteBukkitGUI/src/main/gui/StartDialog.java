/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.gui;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class StartDialog extends javax.swing.JFrame {

	
    public StartDialog() {
        initComponents();

        version.setText("Remote access client for Minecraft");

        setLocationRelativeTo(null);

        ((AbstractDocument) port.getDocument()).setDocumentFilter(new NumbersOnlyDocumentFilter());
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        prog = new javax.swing.JProgressBar();
        launchButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        host = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        port = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        username = new javax.swing.JTextField();
        version = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        remember = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("\uD835\uDDE7\uD835\uDDF2\uD835\uDDF9\uD835\uDDFB\uD835\uDDF2\uD835\uDE01\uD835\uDDE0\uD835\uDDD6 Enter your details to connect");

        prog.setString("");
        prog.setStringPainted(true);

        launchButton.setText("Connect");
        launchButton.addActionListener(this::launch);

        jLabel1.setText("Host:");

        host.addActionListener(this::launch);

        jLabel2.setText("Port:");

        jLabel3.setText("Username:");

        jLabel4.setText("Password:");

        port.addActionListener(this::launch);

        password.addActionListener(this::launch);

        username.addActionListener(this::launch);

        version.setText("TelnetMC");

        jLabel5.setText("TelnetMC");

        remember.setText("Remember my credentials");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(password)
                            .addComponent(username)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(port))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(version))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(launchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(remember)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(host, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(launchButton)
                    .addComponent(remember))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(version, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void launch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launch
        connect();
    }//GEN-LAST:event_launch

    public void connect() {
        if (host.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a hostname.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (port.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a port number.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (username.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (new String(password.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            launchButton.setEnabled(false);
            host.setEnabled(false);
            port.setEnabled(false);
            username.setEnabled(false);
            password.setEnabled(false);

            Properties p = new Properties();

            if (remember.isSelected()) {
                p.setProperty("host", getHost().getText());
                p.setProperty("port", getPort().getText());
                p.setProperty("username", getUsername().getText());
                p.setProperty("password", new String(getPassword().getPassword()));
            }

            try {
                p.store(new FileWriter(Main.getCacheFile()), null);
            } catch (IOException ignored) {
            }

            new ServerConnectionThread(this).start();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField host;
    private javax.swing.JButton launchButton;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField port;
    private javax.swing.JProgressBar prog;
    private javax.swing.JCheckBox remember;
    private javax.swing.JTextField username;
    private javax.swing.JLabel version;
    // End of variables declaration//GEN-END:variables

    public JTextField getHost() {
        return host;
    }

    public JTextField getPort() {
        return port;
    }

    public JProgressBar getProg() {
        return prog;
    }

    public JTextField getUsername() {
        return username;
    }
    
    public void Encrypt() {
    	password.setText(Main.SHA512(Arrays.toString(password.getPassword())));
    }

    public JPasswordField getPassword() {
    	if(Main.doEncrypt) {
    		Encrypt();
    	}
        return password;
    }

    public JCheckBox getRemember() {
        return remember;
    }
}
