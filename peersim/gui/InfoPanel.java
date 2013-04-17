package peersim.gui;

import edu.umd.cs.piccolo.PNode;
import java.awt.Color;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Dimitris
 */
public class InfoPanel extends JPanel {

    private JLabel NodeLabel = new JLabel("<html>Currently <font color='green'>Selected</font> Node ID:</html>");
    private JLabel PredLabel = new JLabel("<html>Current <font color='blue'>Predecessor</font> Node ID:</html>");
    private JLabel SuccLabel = new JLabel("<html>Current <font color='red'>Successor</font> Node ID:</html>");
    private JLabel NodeId = new JLabel("none");
    private JLabel PredId = new JLabel("none");
    private JLabel SuccId = new JLabel("none");
    private JLabel SecretLabel = new JLabel("");
    private JLabel stepLabel = new JLabel("Event step:");
    private JLabel gotoLabel = new JLabel("Goto event:");
    private JLabel selectNodeLabel = new JLabel("Move node:");
    private JButton backButton = new JButton("<");
    private JButton fwdButton = new JButton(">");
    private JButton nextNodeButton = new JButton("Next");
    private JButton previousNodeButton = new JButton("Previous");
    private JTextField stepTextField = new JTextField(5);
    private JTextField gotoTextField = new JTextField();
    private JPanel FingPanel = new JPanel(new MigLayout("wrap 1"));
    private JPanel ControlPanel = new JPanel(new MigLayout("wrap 1"));
    private JPanel InfoPanel = new JPanel(new MigLayout("wrap 1"));
    private JPanel NewlyAddedPanel = new JPanel(new MigLayout("wrap 1"));

    public InfoPanel() {
        super();
        setLayout(new MigLayout("wrap 1"));
        stepTextField.setText("1");
        stepTextField.setHorizontalAlignment(JTextField.RIGHT);
        gotoTextField.setText("0");
        gotoTextField.setHorizontalAlignment(JTextField.RIGHT);

        ControlPanel.add(gotoLabel, "split 2");
        ControlPanel.add(gotoTextField, "grow");
        ControlPanel.add(stepLabel, "split 4");
        ControlPanel.add(backButton, "shrink");
        ControlPanel.add(stepTextField, "grow");
        ControlPanel.add(fwdButton, "shrink");
        ControlPanel.add(selectNodeLabel, "split 3");
        ControlPanel.add(previousNodeButton, "shrink");
        ControlPanel.add(nextNodeButton, "shrink");
        ControlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        add(ControlPanel, "gapleft");

        InfoPanel.add(SecretLabel);
        InfoPanel.add(NodeLabel, "grow");
        InfoPanel.add(NodeId);
        InfoPanel.add(PredLabel);
        InfoPanel.add(PredId);
        InfoPanel.add(SuccLabel);
        InfoPanel.add(SuccId);
        InfoPanel.setBorder(BorderFactory.createTitledBorder("Neighbors"));
        add(InfoPanel, "gapleft");

        FingPanel.setBorder(BorderFactory.createTitledBorder("Finger Nodes"));
        resetFingers();
        add(FingPanel, "gapleft");

        NewlyAddedPanel.setBorder(BorderFactory.createTitledBorder("Newly Added Nodes"));
        resetNewlyAdded();
        add(NewlyAddedPanel, "gapleft");
        
        setBorder(BorderFactory.createTitledBorder("Controls & Information"));
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
    
    public void setSecretLabel(){
        SecretLabel.setText(" ");
        SecretLabel.setText("");
    }

    public void addFingersToPanel(ArrayList<PNode> arrayList) {
        FingPanel.removeAll();
        for (int i = 0; i < arrayList.size(); i++) {
            if ((PNode) arrayList.get(i) != null) {
                FingPanel.add(new JLabel("<html>Finger <font color='orange'>node</font> ID:</html>"), "align left");
                FingPanel.add(new JLabel(((BigInteger) ((PNode) arrayList.get(i)).getAttribute("chordId")).toString(16)), "align left");
            }
        }
    }
    
    public void addNewlyAddedNodes(ArrayList<PNode> arrayList) {
        NewlyAddedPanel.removeAll();
        for (int i = 0; i < arrayList.size(); i++) {
            if ((PNode) arrayList.get(i) != null) {
                NewlyAddedPanel.add(new JLabel("<html>Newly added <font color='purple'>node</font> ID:</html>"), "align left");
                NewlyAddedPanel.add(new JLabel(((BigInteger) ((PNode) arrayList.get(i)).getAttribute("chordId")).toString(16)), "align left");
            }
        }
    }

    public void resetFingers() {
        FingPanel.removeAll();
        FingPanel.add(new JLabel("none                         "));
    }
    
    public void resetNewlyAdded() {
        NewlyAddedPanel.removeAll();
        NewlyAddedPanel.add(new JLabel("none                         "));
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
