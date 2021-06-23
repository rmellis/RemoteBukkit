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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

import static java.awt.Color.getHSBColor;
/**
 * @author Keeley Hoek (escortkeel)
 * @update rmellis - TelnetMC
 **/
public class ColorPane extends JTextPane {

    private static final Color D_Black = getHSBColor(0.000f, 0.000f, 0.000f);
    private static final Color D_Red = getHSBColor(0.000f, 1.000f, 0.502f);
    private static final Color D_Blue = getHSBColor(0.667f, 1.000f, 0.502f);
    private static final Color D_Magenta = getHSBColor(0.833f, 1.000f, 0.502f);
    private static final Color D_Green = getHSBColor(0.333f, 1.000f, 0.502f);
    private static final Color D_Yellow = getHSBColor(0.167f, 1.000f, 0.502f);
    private static final Color D_Cyan = getHSBColor(0.500f, 1.000f, 0.502f);
    private static final Color D_White = getHSBColor(0.000f, 0.000f, 0.753f);
    private static final Color B_Black = getHSBColor(0.000f, 0.000f, 0.502f);
    private static final Color B_Red = getHSBColor(0.000f, 1.000f, 1.000f);
    private static final Color B_Blue = getHSBColor(0.667f, 1.000f, 1.000f);
    private static final Color B_Magenta = getHSBColor(0.833f, 1.000f, 1.000f);
    private static final Color B_Green = getHSBColor(0.333f, 1.000f, 1.000f);
    private static final Color B_Yellow = getHSBColor(0.167f, 1.000f, 1.000f);
    private static final Color B_Cyan = getHSBColor(0.500f, 1.000f, 1.000f);
    private static final Color B_White = getHSBColor(0.000f, 0.000f, 1.000f);
    private static final Color cReset = D_White;
    private static Color colorCurrent = cReset;
    private String remaining = "";

    public void append(Color c, String s) throws BadLocationException {
        getStyledDocument().insertString(getStyledDocument().getLength(), s,
                StyleContext.getDefaultStyleContext().addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c));
    }

    public synchronized void appendANSI(String s) throws BadLocationException {
        int aPos = 0;
        String addString = remaining + s;
        remaining = "";

        if (addString.length() > 0) {
            int aIndex = addString.indexOf("\u001B");

            if (aIndex == -1) {
                append(colorCurrent, addString);
                return;
            } else if (aIndex > 0) {
                append(colorCurrent, addString.substring(0, aIndex));
                aPos = aIndex;
            }

            boolean stillSearching = true;
            while (stillSearching) {
                int mIndex = addString.indexOf("m", aPos);
                if (mIndex < 0) {
                    remaining = addString.substring(aPos);
                    stillSearching = false;
                    continue;
                } else {
                    colorCurrent = getANSIColor(addString.substring(aPos, mIndex + 1));
                }
                aPos = mIndex + 1;

                aIndex = addString.indexOf("\u001B", aPos);

                if (aIndex == -1) {
                    append(colorCurrent, addString.substring(aPos));
                    stillSearching = false;
                    continue;
                }

                append(colorCurrent, addString.substring(aPos, aIndex));

                aPos = aIndex;
            }
        }
    }

    public static Color getANSIColor(String ansiColor) {
        if (ansiColor.equals("\u001B[30;1m")) {
            return B_Black;
        } else if (ansiColor.equals("\u001B[31;1m")) {
            return B_Red;
        } else if (ansiColor.equals("\u001B[32;1m")) {
            return B_Green;
        } else if (ansiColor.equals("\u001B[33;1m")) {
            return B_Yellow;
        } else if (ansiColor.equals("\u001B[34;1m")) {
            return B_Blue;
        } else if (ansiColor.equals("\u001B[35;1m")) {
            return B_Magenta;
        } else if (ansiColor.equals("\u001B[36;1m")) {
            return B_Cyan;
        } else if (ansiColor.equals("\u001B[37;1m")) {
            return B_White;
        } else if (ansiColor.startsWith("\u001B[30")) {
            return D_Black;
        } else if (ansiColor.startsWith("\u001B[31")) {
            return D_Red;
        } else if (ansiColor.startsWith("\u001B[32")) {
            return D_Green;
        } else if (ansiColor.startsWith("\u001B[33")) {
            return D_Yellow;
        } else if (ansiColor.startsWith("\u001B[34")) {
            return D_Blue;
        } else if (ansiColor.startsWith("\u001B[35")) {
            return D_Magenta;
        } else if (ansiColor.startsWith("\u001B[36")) {
            return D_Cyan;
        } else if (ansiColor.startsWith("\u001B[37")) {
            return D_White;
        } else if (ansiColor.equals("\u001B[m")) {
            return cReset;
        } else {
            return B_White;
        }
    }
}