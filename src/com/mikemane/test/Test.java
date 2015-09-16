/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mikemane.test;

import com.mikemane.gui.MainView;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author udoka
 */
public class Test {

    public final static String JAVA_VERSION = "1.8";
    public final static String OS = "Windows";

    public static void main(String[] args) {
        if (System.getProperty("java.version").contains(JAVA_VERSION)) {
            if (System.getProperty("os.name").contains(OS)) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    System.out.println(ex.getCause() + " Message " + ex.getMessage());
                }
            }
            SwingUtilities.invokeLater(() -> {
                MainView v = new MainView();
            });
        } else {
            JOptionPane.showMessageDialog(null, "Please Install java 1.8 or above thanks");
        }

    }

}
