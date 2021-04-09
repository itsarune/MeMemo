package ui;

import ui.gui.MeMemoGUI;

import javax.swing.*;

/**
 * Starts the user interface.
 */
public class Main {
    //EFFECTS: starts the user interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MeMemoGUI();
            }
        });
    }
}
