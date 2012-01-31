/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

/**
 *
 * @author Dimitris
 */
public class Main{
    
    public static void main(String[] args){
        Thread sim = new Thread(new SimulatorRunnable(args), "SIM");
        sim.start();
        try{
            sim.join();
        } catch(InterruptedException e){
            System.err.println("ERROR: Thread "+ sim.getName() + " was interrupted!");
        }
        NetworkHistory.printHistory();
        Thread gui = new GUIThread(new GUIRunnable(), "GUI");
        gui.start();
    }
    
}
