/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Dimitris
 */
public class ChordFrame extends JFrame{
    
    public ChordFrame(){
        super("Chord Viz");
        this.setLayout(new BorderLayout());
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        JPanel buttonPanel = new JPanel(new GridLayout(1,0));
        JButton backButton = new JButton("Previous Event");
        JButton fwdButton = new JButton("Next Event");
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        InfoPanel infoPanel = new InfoPanel();
        this.add(infoPanel, BorderLayout.WEST);
        this.add(new ChordCanvas(infoPanel, backButton, fwdButton), BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}
