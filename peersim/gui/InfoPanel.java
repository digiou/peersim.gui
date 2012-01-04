
package peersim.gui;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Dimitris
 */
public class InfoPanel extends JPanel{
    
    JLabel NodeId = new JLabel("Current node Id");
    JLabel PredId = new JLabel("Predecessor node Id");
    JLabel SuccId = new JLabel("Succesor node Id");
    JPanel PredPanel = new JPanel();
    JPanel SuccPanel = new JPanel();
    
    public InfoPanel(){
        super();
        setLayout(new GridLayout(0,1));
        add(NodeId);
        PredPanel.setBackground(Color.red);
        PredId.setForeground(Color.white);
        PredPanel.add(PredId);
        add(PredPanel);
        SuccPanel.setBackground(Color.blue);
        SuccId.setForeground(Color.white);
        SuccPanel.add(SuccId);
        add(SuccPanel);
        setVisible(true);
    }
    
}
