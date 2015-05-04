/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * @author Dimitris
 */
public class ChordFrame extends JFrame{
    
    public ChordFrame(){
        super("PViz");
        setLayout(new BorderLayout());
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
        InfoPanel infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.WEST);
        add(new ChordCanvas(infoPanel), BorderLayout.CENTER);
        pack();
        setVisible(true);
    } 
}
