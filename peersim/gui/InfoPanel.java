
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
    private Color originalColor;
    
    public InfoPanel(){
        super();
        setLayout(new GridLayout(0,1));
        add(NodeId);
        
        originalColor = PredPanel.getBackground();
        
        PredPanel.add(PredId);
        add(PredPanel);
        
        
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
        PredPanel.setBackground(Color.RED);
        PredId.setForeground(Color.WHITE);
        PredId.setText(newID);
    }
    
    public void setSuccId(String newID){
        SuccPanel.setBackground(Color.BLUE);
        SuccId.setForeground(Color.WHITE);
        SuccId.setText(newID);
    }
    
    public void addFingersToPanel(int fingers){
        for(int i = 0;i<fingers;i++){
            FingPanel.add(new JLabel("Finger node Id"));
        }
    }
    
    public void resetNodeId(){
        NodeId.setText("Current node Id");
    }
    
    public void resetPredId(){
        PredPanel.setBackground(originalColor);
        PredId.setForeground(Color.BLACK);
        PredId.setText("Predecessor node Id");
    }
    
    public void resetSuccId(){
        SuccPanel.setBackground(originalColor);
        SuccId.setForeground(Color.BLACK);
        SuccId.setText("Successor node Id");
    }
    
    public void resetPanel(){
        resetNodeId();
        resetPredId();
        resetSuccId();
    }
    
}
