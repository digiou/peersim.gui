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
    }
    
}
