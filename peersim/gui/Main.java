/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import javax.swing.UIManager;

/**
 *
 * @author Dimitris
 */
public class Main{
    
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error while setting the look and feel");
        }
        Thread gui = new GUIThread(new GUIRunnable(), "GUI");
        gui.start();
    }
    
}
