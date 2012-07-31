/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Dimitris
 */
public class ChordFrame extends JFrame{
    
    public ChordFrame(){
        super("Chord Viz");
        this.setLayout(new BorderLayout());
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        JPanel southPanel = new JPanel(new GridLayout(1,0));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1,0));
        JButton backButton = new JButton();
        JButton fwdButton = new JButton();
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        
        JPanel stepPanel = new JPanel(new GridLayout(1,0));
        JLabel stepLabel = new JLabel("Steps: ");
        JTextField stepTextField = new JTextField("1");
        stepPanel.add(stepLabel);
        stepPanel.add(stepTextField);
        
        southPanel.add(buttonPanel);
        southPanel.add(stepPanel);
        this.add(southPanel, BorderLayout.SOUTH);
        
        InfoPanel infoPanel = new InfoPanel();
        this.add(infoPanel, BorderLayout.WEST);
        this.add(new ChordCanvas(infoPanel, backButton, fwdButton, stepTextField), BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}
