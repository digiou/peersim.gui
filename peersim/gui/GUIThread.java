/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

/**
 *
 * @author Dimitris
 */
public class GUIThread extends Thread{
    
    public GUIThread(){
        super();
    }
    
    public GUIThread(GUIRunnable runnable){
        super(runnable);
    }
    
    public GUIThread(GUIRunnable runnable, String name){
        super(runnable, name);
    }
}
