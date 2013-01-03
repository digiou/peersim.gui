package peersim.gui;

import edu.umd.cs.piccolo.PNode;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Dimitris
 */
public class InfoPanel extends JPanel {

    private JLabel NodeLabel = new JLabel("Currently selected Node ID:");
    private JLabel PredLabel = new JLabel("Current Predecessor Node ID:");
    private JLabel SuccLabel = new JLabel("Current Successor Node ID:");
    private JLabel NodeId = new JLabel("none");
    private JLabel PredId = new JLabel("none");
    private JLabel SuccId = new JLabel("none");
    private JLabel FingId = new JLabel("Finger node IDs:");
    private JLabel stepLabel = new JLabel("Steps:");
    private JButton backButton = new JButton("Back 1 event");
    private JButton fwdButton = new JButton("Move 1 event");
    private JTextField stepTextField = new JTextField("1");
    private JPanel NodePanel = new JPanel(new GridLayout(0, 1));
    private JPanel PredPanel = new JPanel(new GridLayout(0, 1));
    private JPanel SuccPanel = new JPanel(new GridLayout(0, 1));
    private JPanel FingPanel = new JPanel(new GridLayout(0, 1));
    private JPanel MasterPanel = new JPanel(new GridLayout(0, 1));
    private JPanel ButtonPanel = new JPanel(new FlowLayout());
    private JPanel StepPanel = new JPanel(new GridLayout(1,0));
    private JPanel controlPanel = new JPanel(new GridLayout(0,1));
    private Color originalColor;

    public InfoPanel() {
        super();
        setLayout(new GridLayout(0, 1));
        originalColor = PredPanel.getBackground();
        
        ButtonPanel.add(backButton);
        ButtonPanel.add(fwdButton);
        
        StepPanel.add(stepLabel);
        StepPanel.add(stepTextField);
        controlPanel.add(StepPanel);
        controlPanel.add(ButtonPanel);
        MasterPanel.add(controlPanel);
        
        NodePanel.add(NodeLabel);
        NodePanel.add(NodeId);
        MasterPanel.add(NodePanel);

        PredPanel.add(PredLabel);
        PredPanel.add(PredId);
        MasterPanel.add(PredPanel);

        SuccPanel.add(SuccLabel);
        SuccPanel.add(SuccId);
        MasterPanel.add(SuccPanel);

        FingPanel.add(FingId);
        MasterPanel.add(FingPanel);
        add(MasterPanel);
        setVisible(true);
    }

    public void setNodeId(String newID) {
        NodeId.setText(newID);
    }

    public void resetNodeId() {
        NodeId.setText("none");
    }

    public void setPredId(String newID) {
        PredPanel.setBackground(Color.RED);
        PredLabel.setForeground(Color.WHITE);
        PredId.setForeground(Color.WHITE);
        PredId.setText(newID);
    }

    public void resetPredId() {
        PredPanel.setBackground(originalColor);
        PredLabel.setForeground(Color.BLACK);
        PredId.setForeground(Color.BLACK);
        PredId.setText("none");
    }

    public void setSuccId(String newID) {
        SuccPanel.setBackground(Color.BLUE);
        SuccLabel.setForeground(Color.WHITE);
        SuccId.setForeground(Color.WHITE);
        SuccId.setText(newID);
    }

    public void resetSuccId() {
        SuccPanel.setBackground(originalColor);
        SuccLabel.setForeground(Color.BLACK);
        SuccId.setForeground(Color.BLACK);
        SuccId.setText("none");
    }

    public void addFingersToPanel(ArrayList<PNode> arrayList) {
        FingPanel.removeAll();
        for (int i = 0; i < arrayList.size(); i++) {
            if ((PNode) arrayList.get(i) != null) {
                FingPanel.add(new JLabel("Finger " + i + " node ID:"));
                FingPanel.add(new JLabel(((BigInteger) ((PNode) arrayList.get(i)).getAttribute("chordId")).toString(16)));
            }
        }
        FingPanel.setBackground(Color.YELLOW);
    }

    public void setNullFingers() {
        FingPanel.removeAll();
        FingPanel.add(new JLabel("No fings yet!"));
    }

    public void resetFingPanel() {
        FingPanel.setBackground(originalColor);
        FingPanel.removeAll();
        FingPanel.add(FingId);
    }

    public void resetInfo() {
        resetNodeId();
        resetPredId();
        resetSuccId();
        resetFingPanel();
    }
    
    public JButton getBackButton(){
        return backButton;
    }
    
    public JButton getFwdButton(){
        return fwdButton;
    }
    
    public JTextField getTxtField(){
        return stepTextField;
    }
}
