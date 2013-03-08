package peersim.gui;

import edu.umd.cs.piccolo.PNode;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Dimitris
 */
public class InfoPanel extends JPanel {

    private JLabel NodeLabel = new JLabel("<html>Currently <font color='green'>Selected</font> Node ID:</html>");
    private JLabel PredLabel = new JLabel("<html>Current <font color='red'>Predecessor</font> Node ID:</html>");
    private JLabel SuccLabel = new JLabel("<html>Current <font color='blue'>Successor</font> Node ID:</html>");
    private JLabel NodeId = new JLabel("none");
    private JLabel PredId = new JLabel("none");
    private JLabel SuccId = new JLabel("none");
    private JLabel FingLabel = new JLabel("Finger Node IDs:");
    private JLabel NoneLabel = new JLabel("none");
    private JLabel stepLabel = new JLabel("Event step:");
    private JLabel gotoLabel = new JLabel("Goto event:");
    private JLabel selectNodeLabel = new JLabel("Move node:");
    private JButton backButton = new JButton("<");
    private JButton fwdButton = new JButton(">");
    private JButton nextNodeButton = new JButton("Next");
    private JButton previousNodeButton = new JButton("Previous");
    private JTextField stepTextField = new JTextField(5);
    private JTextField gotoTextField = new JTextField();
    private JPanel FingPanel = new JPanel(new GridLayout(0,1));

    public InfoPanel() {
        super();
        setLayout(new MigLayout("wrap 1"));
        stepTextField.setText("1");
        stepTextField.setHorizontalAlignment(JTextField.RIGHT);
        gotoTextField.setText("0");
        gotoTextField.setHorizontalAlignment(JTextField.RIGHT);
        
        add(gotoLabel, "split 2");
        add(gotoTextField, "grow");
        add(stepLabel, "split 4");
        add(backButton, "shrink");
        add(stepTextField, "grow");
        add(fwdButton, "shrink");
        add(selectNodeLabel, "split 3");
        add(previousNodeButton, "shrink");
        add(nextNodeButton, "shrink");
        add(NodeLabel);
        add(NodeId);
        add(PredLabel);
        add(PredId);
        add(SuccLabel);
        add(SuccId);
        add(FingLabel);
        add(NoneLabel);
        add(FingPanel, "gapleft");        
        setVisible(true);
    }

    public void setNodeId(String newID) {
        NodeId.setText(newID);
    }

    public void resetNodeId() {
        NodeId.setText("none");
    }

    public void setPredId(String newID) {
        PredId.setText(newID);
    }

    public void resetPredId() {
        PredId.setText("none");
    }

    public void setSuccId(String newID) {
        SuccId.setText(newID);
    }

    public void resetSuccId() {
        SuccId.setText("none");
    }
    
    public void addFingersToPanel(ArrayList<PNode> arrayList) {
        remove(NoneLabel);
        FingPanel.removeAll();
        for (int i = 0; i < arrayList.size(); i++) {
            if ((PNode) arrayList.get(i) != null) {
                FingPanel.add(new JLabel("Finger " + Integer.toString(i) + " node ID:"), "align left");
                FingPanel.add(new JLabel(((BigInteger) ((PNode) arrayList.get(i)).getAttribute("chordId")).toString(16)), "align left");
            }
        }
    }

    public void resetFingers() {
        FingPanel.removeAll();
        add(NoneLabel);
    }

    public void resetInfo() {
        resetNodeId();
        resetPredId();
        resetSuccId();
        resetFingers();
    }
    
    public JTextField getStepTxtField() {
        return stepTextField;
    }
    
    public JTextField getGotoTxtField() {
        return gotoTextField;
    }

    
    public JButton getBackButton() {
        return backButton;
    }

    public JButton getFwdButton() {
        return fwdButton;
    }
    
    public JButton getNextNodeButton() {
        return nextNodeButton;
    }
    
    public JButton getPreviousNodeButton() {
        return previousNodeButton;
    }
}
