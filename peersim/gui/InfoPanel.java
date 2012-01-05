
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
    
    private JLabel NodeId = new JLabel("Current node Id");
    private JLabel PredId = new JLabel("Predecessor node Id");
    private JLabel SuccId = new JLabel("Succesor node Id");
    private JLabel FingId = new JLabel("Finger node Id");
    private JPanel PredPanel = new JPanel();
    private JPanel SuccPanel = new JPanel();
    private JPanel FingPanel = new JPanel();
    
    public InfoPanel(){
        super();
        setLayout(new GridLayout(0,1));
        add(NodeId);
        
        PredPanel.setBackground(Color.RED);
        PredId.setForeground(Color.WHITE);
        PredPanel.add(PredId);
        add(PredPanel);
        
        SuccPanel.setBackground(Color.BLUE);
        SuccId.setForeground(Color.WHITE);
        SuccPanel.add(SuccId);
        add(SuccPanel);
        
        FingPanel.setBackground(Color.YELLOW);
        FingPanel.add(FingId);
        add(FingPanel);
        setVisible(true);
    }
    
    public void setNodeId(String newID){
        NodeId.setText(newID);
    }
    
    public void setPredId(String newID){
        PredId.setText(newID);
    }
    
    public void setSuccId(String newID){
        SuccId.setText(newID);
    }
    
    public void resetNodeId(){
        NodeId.setText("Current node Id");
    }
    
    public void resetPredId(){
        PredId.setText("Predecessor node Id");
    }
    
    public void resetSuccId(){
        SuccId.setText("Successor node Id");
    }
    
}
