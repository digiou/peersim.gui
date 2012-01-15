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
        Thread T = new Thread(new SimulatorRunnable(args), "Test");
        T.start();
        try{
            T.join();
        } catch(InterruptedException e){
            System.err.println("ERROR: Thread "+ T.getName() + " was interrupted!");
        }
        
        System.out.println("Done!");
    }
    
}
