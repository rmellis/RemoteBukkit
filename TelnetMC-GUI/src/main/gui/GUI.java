/*
 * Copyright (c) 2013, Keeley Hoek - Simplified BSD License
 * Copyright (c) 2021, rmellis - TelnetMC
 * All rights reserved.
 */
package me.escortkeel.remotebukkit.gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.ArrayList;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class GUI extends JFrame {

    private final PrintStream out;
    private final ArrayList<String> cache = new ArrayList<>();
    private int cacheIndex = -1;
    private String preCache;

    public GUI(PrintStream out) {
        this.out = out;

        initComponents();

        inputField.requestFocusInWindow();

        ((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setLocationRelativeTo(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        inputField = new javax.swing.JTextField();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        console = new me.escortkeel.remotebukkit.gui.ColorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TelnetMC - Console");
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus();
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        jLabel1.setText(">");

        inputField.setBackground(new java.awt.Color(35, 39, 42));
        inputField.setForeground(new java.awt.Color(255, 255, 255));
        inputField.addActionListener(this::inputFieldActionPerformed);
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });

        console.setBackground(new java.awt.Color(35, 39, 42));
        console.setEditable(false);
        console.setUI(new javax.swing.plaf.basic.BasicEditorPaneUI());
        jScrollPane2.setViewportView(console);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputField, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputFieldActionPerformed
        if (!evt.getActionCommand().isEmpty()) {
            String command = evt.getActionCommand();
            
            if(command.startsWith("/") && command.length() > 1) {
                command = command.substring(1);
            }
            
            out.println(command);
            cache.add(0, command);

            inputField.setText("");
        }
    }//GEN-LAST:event_inputFieldActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (cacheIndex == -1) {
                preCache = inputField.getText();
            }

            cacheIndex++;

            if (cacheIndex < cache.size()) {
                inputField.setText(cache.get(cacheIndex));
            } else {
                cacheIndex = cache.size() - 1;
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            cacheIndex--;

            if (cacheIndex == -1) {
                inputField.setText(preCache);
            } else if (cacheIndex > -1) {
                inputField.setText(cache.get(cacheIndex));
            } else {
                cacheIndex = -1;
            }
        } else {
            cacheIndex = -1;
        }
    }//GEN-LAST:event_inputFieldKeyPressed

    private void formWindowGainedFocus() {//GEN-FIRST:event_formWindowGainedFocus
        inputField.requestFocusInWindow();
    }//GEN-LAST:event_formWindowGainedFocus

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private me.escortkeel.remotebukkit.gui.ColorPane console;
    private javax.swing.JTextField inputField;
    // End of variables declaration//GEN-END:variables

    public ColorPane getConsole() {
        return console;
    }
}
