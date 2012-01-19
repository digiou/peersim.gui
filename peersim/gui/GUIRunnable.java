/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

/**
 *
 * @author Dimitris
 */
public class GUIRunnable implements Runnable {
    
    public GUIRunnable(){
        super();
    }

    @Override
    public void run() {
        ChordFrame frame = new ChordFrame();
    }
    
}
