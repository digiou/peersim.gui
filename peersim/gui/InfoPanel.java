
package peersim.gui;

import edu.umd.cs.piccolo.PNode;
import java.awt.Color;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.ArrayList;
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
    private JPanel NodePanel = new JPanel();
    private JPanel PredPanel = new JPanel();
    private JPanel SuccPanel = new JPanel();
    private JPanel FingPanel = new JPanel();
    private Color originalColor;
    
    public InfoPanel(){
        super();
        setLayout(new GridLayout(0,1));
        originalColor = PredPanel.getBackground();
        
        NodePanel.add(NodeId);
        add(NodePanel);
        
        PredPanel.add(PredId);
        add(PredPanel);
        
        SuccPanel.add(SuccId);
        add(SuccPanel);
        FingPanel.setLayout(new GridLayout(0,1));
        FingPanel.add(FingId);
        add(FingPanel);
        setVisible(true);
    }
    
    public void setNodeId(String newID){
        NodeId.setText(newID);
    }
    
    public void resetNodeId(){
        NodeId.setText("Current node Id");
    }
    
    public void setPredId(String newID){
        PredPanel.setBackground(Color.RED);
        PredId.setForeground(Color.WHITE);
        PredId.setText(newID);
    }
    
    public void resetPredId(){
        PredPanel.setBackground(originalColor);
        PredId.setForeground(Color.BLACK);
        PredId.setText("Predecessor node Id");
    }
    
    public void setSuccId(String newID){
        SuccPanel.setBackground(Color.BLUE);
        SuccId.setForeground(Color.WHITE);
        SuccId.setText(newID);
    }
    
    public void resetSuccId(){
        SuccPanel.setBackground(originalColor);
        SuccId.setForeground(Color.BLACK);
        SuccId.setText("Successor node Id");
    }
    
    public void addFingersToPanel(ArrayList<PNode> arrayList){
        FingPanel.removeAll();
        for(int i = 0;i<arrayList.size();i++){
            FingPanel.add(new JLabel(((BigInteger)((PNode)arrayList.get(i)).getAttribute("chordId")).toString(16)));
        }
        FingPanel.setBackground(Color.YELLOW);
    }
    
    public void resetFingPanel(){
        FingPanel.setBackground(originalColor);
        FingPanel.removeAll();
        FingPanel.add(FingId);
    }
    
    public void resetPanel(){
        resetNodeId();
        resetPredId();
        resetSuccId();
        resetFingPanel();
    }
    
}
