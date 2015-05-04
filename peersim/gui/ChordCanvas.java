package peersim.gui;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author jim
 */
public class ChordCanvas extends PCanvas {

    private Dimension SCREEN = new Dimension(800, 600);
    private int historySize = NetworkHistory.getSize() - 1;
    private int margin = 50;
    private int radius = SCREEN.width / 2;
    private int current = 0;
    private int step = 1;
    private float cx = margin + radius;
    private float cy = margin + radius;
    private long pinnedNodeSimID = -1;
    private PCamera camera;
    private Point2D epicenter = new Point2D.Double(cx, cy);
    private HashMap<Long, PNode> hashmap;
    private TreeMap<BigInteger, Long> chordIDTreeMap;
    private PLayer nodeLayer = this.getLayer();
    private PLayer edgeLayer = new PLayer();
    private InfoPanel panel;
    private HistoryObject currentNetwork, currentDiff;
    private JButton back, frwrd, nextNodeButton, previousNodeButton;
    private JTextField gotoField, stepField;
    private PBasicInputEventHandler mouseColors, tooltip;
    private PText eventTooltipNode, selectedTooltipNode;
    private ArrayList lines = new ArrayList();
    private PInputEventFilter mouseFilter = new PInputEventFilter();
    private Boolean selected = true;
    private Boolean highlighting = false;
    private PNode selectedNode, highlightedNode;
    private HashSet diffHashSet;
    private ArrayList<PNode> newlyAddedNodes;

    public ChordCanvas(InfoPanel inheritedPanel) {
        super();
        setSize(SCREEN);
        panel = inheritedPanel;
        back = inheritedPanel.getBackButton();
        frwrd = inheritedPanel.getFwdButton();
        nextNodeButton = inheritedPanel.getNextNodeButton();
        previousNodeButton = inheritedPanel.getPreviousNodeButton();
        stepField = inheritedPanel.getStepTxtField();
        gotoField = inheritedPanel.getGotoTxtField();

        this.back.setEnabled(false);
        if (historySize == 0) {
            this.frwrd.setEnabled(false);
        }

        camera = this.getCamera();
        this.getRoot().addChild(edgeLayer);
        camera.addLayer(0, edgeLayer);

        eventTooltipNode = new PText();
        eventTooltipNode.setPickable(false);
        camera.addChild(eventTooltipNode);
        eventTooltipNode.setOffset(0, 0);
        eventTooltipNode.setText("Current event: event @ time: time");

        selectedTooltipNode = new PText();
        selectedTooltipNode.setPickable(false);
        camera.addChild(selectedTooltipNode);
        selectedTooltipNode.setOffset(0, 15);
        selectedTooltipNode.setVisible(false);

        draw(current);

        stepField.getDocument().addDocumentListener(new StepUpdater());
        gotoField.getDocument().addDocumentListener(new GotoUpdater());
        frwrd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawNext(step);
            }
        });
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPrevious(step);
            }
        });

        Action drawNext = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawNext(step);
            }
        };
        frwrd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "drawNext");
        frwrd.getActionMap().put("drawNext", drawNext);

        Action drawPrevious = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPrevious(step);
            }
        };
        back.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "drawPrevious");
        back.getActionMap().put("drawPrevious", drawPrevious);


        nextNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNext();
            }
        });
        previousNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPrevious();
            }
        });

        Action selectNext = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNext();
            }
        };
        nextNodeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "selectNext");
        nextNodeButton.getActionMap().put("selectNext", selectNext);

        Action selectPrevious = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                selectPrevious();
            }
        };
        previousNodeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "selectPrevious");
        previousNodeButton.getActionMap().put("selectPrevious", selectPrevious);

        this.setVisible(true);
    }

    private HashMap<Long, PNode> getRelationships() {
        return hashmap;
    }

    private void draw(int pointer) {
        currentNetwork = NetworkHistory.getEntry(pointer);
        if (currentNetwork.getReason().equals("addition")) {
            currentDiff = NetworkHistory.getDiff(pointer - 1);
            diffHashSet = new HashSet();
            for (int i = 0; i < currentDiff.size(); i++) {
                diffHashSet.add(currentDiff.getNode(i).getID());
            }
        }
        eventTooltipNode.setText("Current event: " + currentNetwork.getReason() + " @time: " + currentNetwork.getTime());
        hashmap = new HashMap<>();
        chordIDTreeMap = new TreeMap<>();
        mouseColors = new MouseEventHandler(mouseFilter);
        tooltip = new TooltipHandler();
        newlyAddedNodes = new ArrayList<>();
        nodeLayer.addInputEventListener(mouseColors);
        nodeLayer.addInputEventListener(tooltip);
        drawNodes(nodeLayer);
    }

    private void drawNodes(PLayer nodeLayer) {
        for (int i = 0; i < currentNetwork.size(); i++) {
            HistoryChordProtocol hcp = (HistoryChordProtocol) currentNetwork.getNode(i).getProtocol();
            chordIDTreeMap.put(hcp.chordId, currentNetwork.getNode(i).getID());
            double angle = getAngle(hcp);
            Circle node;
            Boolean isNew = false;
            if (currentNetwork.getReason().equals("addition")) {
                if (diffHashSet.contains(currentNetwork.getNode(i).getID())) {
                    isNew = true;
                }
            }
            node = new Circle(angle, isNew);
            hashmap.put(currentNetwork.getNode(i).getID(), node);
            storeInfo(node, hcp, currentNetwork.getNode(i).getID());
            nodeLayer.addChild(node);
            if (isNew) {
                node.moveToFront();
                newlyAddedNodes.add((PNode)node);
            }
        }
        panelCheckNewlyAdded(newlyAddedNodes);
        if (pinnedNodeSimID != -1) {
            if ((PNode) getRelationships().get(pinnedNodeSimID) != null) {
                colorizeKeyboard((PNode) getRelationships().get(pinnedNodeSimID));
            }
        }
    }

    private void storeInfo(PNode node, HistoryChordProtocol hcp, long SimID) {
        if (hcp.predecessor.equals(SimID)) {
            node.addAttribute("predecessor", "null");/*
             * node just added, I put the node's simulation ID if predecessor is
             * not yet found by ChordProtocol controls
             */
        } else {
            node.addAttribute("predecessor", hcp.predecessor);
        }

        long succId = hcp.successorList[0];
        if (hcp.successorList[0].equals(SimID)) {
            node.addAttribute("successor", "null");
        } else {
            node.addAttribute("successor", succId);
        }

        node.addAttribute("fingers", new ArrayList());
        int SIZE = hcp.fingerTable.length;
        long previousID = 0;
        for (int i = 0; i < SIZE; i++) {
            if (!(hcp.fingerTable[i] == succId || hcp.fingerTable[i] == SimID || hcp.fingerTable[i] == previousID)) {
                ((ArrayList) node.getAttribute("fingers")).add(hcp.fingerTable[i]);
                previousID = hcp.fingerTable[i];
            }
        }
        node.addAttribute("chordId", hcp.chordId);
        node.addAttribute("simID", SimID);
    }

    private double getAngle(HistoryChordProtocol hcp) {
        double chordId = hcp.chordId.doubleValue();
        double maxValue = Math.pow(2, hcp.m);
        double degree = ((chordId / maxValue) * 365);
        double radian = degree / 180 * Math.PI;
        return radian;
    }

    private PPath drawLine(PNode start, PNode end) {
        PPath line = PPath.createLine((float) start.getFullBoundsReference().getCenter2D().getX(),
                (float) start.getFullBoundsReference().getCenter2D().getY(),
                (float) end.getFullBoundsReference().getCenter2D().getX(),
                (float) end.getFullBoundsReference().getCenter2D().getY());
        line.setStroke(new PFixedWidthStroke());
        return line;
    }

    private PPath drawCurvedLine(PNode start, PNode end, double numerator, double denumerator) {
        Point2D centerStart = start.getFullBoundsReference().getCenter2D();
        Point2D centerEnd = end.getFullBoundsReference().getCenter2D();
        Point2D midPoint = midpoint(centerStart, centerEnd);
        double div = (numerator / denumerator);
        Point2D newPoint = pointBetween(midPoint, epicenter, div);
        PPath line = new PPath();
        line.moveTo((float) centerStart.getX(), (float) centerStart.getY());
        line.curveTo((float) centerStart.getX(), (float) centerStart.getY(), (float) newPoint.getX(), (float) newPoint.getY(), (float) centerEnd.getX(), (float) centerEnd.getY());
        line.setStroke(new PFixedWidthStroke());
        return line;
    }

    private Point2D midpoint(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 2,
                p1.getY() + (p2.getY() - p1.getY()) / 2);
    }

    private Point2D pointBetween(Point2D p1, Point2D p2, double div) {
        return new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / div,
                p1.getY() + (p2.getY() - p1.getY()) / div);
    }

    public String tooltipText(PNode aNode) {
        String tooltipText;
        String chordId = ((BigInteger) aNode.getAttribute("chordId")).toString(16);
        tooltipText = "Node ID#: " + chordId + "\n";
        return tooltipText;
    }

    private void giveInfoToPanel(PNode thisNode, PNode succ, PNode pred, ArrayList<PNode> arraylist) {
        panel.setNodeId(((BigInteger) thisNode.getAttribute("chordId")).toString(16));
        if (pred != null) {
            panel.setPredId(((BigInteger) pred.getAttribute("chordId")).toString(16));
        } else {
            panel.setPredId("Not def yet!");
        }

        if (succ != null) {
            panel.setSuccId(((BigInteger) succ.getAttribute("chordId")).toString(16));
        } else {
            panel.setSuccId("Not def yet!");
        }

        if (arraylist != null) {
            if (arraylist.get(0) != null) {
                panel.addFingersToPanel(arraylist);
            } else {
                panel.resetFingers();
            }
        }

    }
    
    private void panelCheckNewlyAdded(ArrayList<PNode> newNodes){
        if (currentNetwork.getReason().equals("addition")) {
            panel.addNewlyAddedNodes(newNodes);
            panel.setSecretLabel();
        } else {
            panel.resetNewlyAdded();
            panel.setSecretLabel();
        }
    }

    private void removeInfoFromPanel() {
        panel.resetInfo();
    }

    private void clearCanvas() {
        edgeLayer.removeAllChildren();
        nodeLayer.removeAllChildren();
        nodeLayer.removeInputEventListener(mouseColors);
        nodeLayer.removeInputEventListener(tooltip);
        removeInfoFromPanel();
    }

    private void drawNext(int steps) {
        clearCanvas();
        if (current + steps <= historySize) {
            current += steps;
        } else {
            current = historySize;
        }
        draw(current);
        if (current >= 1) {
            back.setEnabled(true);
        }
        if (current == historySize) {
            frwrd.setEnabled(false);
        }
    }

    private void drawPrevious(int steps) {
        clearCanvas();
        if (current - steps >= 0) {
            current -= steps;
        } else {
            current = 0;
        }
        draw(current);
        if (current == 0) {
            back.setEnabled(false);
        }
        if (current <= historySize - 1) {
            frwrd.setEnabled(true);
        }
    }

    private void drawGoto(int destination) {
        if (destination != current) {
            clearCanvas();
            if (destination >= historySize) {
                current = historySize;
                draw(historySize);
                back.setEnabled(true);
                frwrd.setEnabled(false);
            } else if (destination <= 0) {
                current = 0;
                draw(0);
                back.setEnabled(false);
                frwrd.setEnabled(true);
            } else {
                current = destination;
                draw(destination);
                back.setEnabled(true);
                frwrd.setEnabled(true);
            }
        }
    }

    private void selectNext() {
        BigInteger firstChordID = chordIDTreeMap.firstKey();
        Long firstSimID = chordIDTreeMap.get(firstChordID);
        PNode firstNode = (PNode) getRelationships().get(firstSimID);
        if (pinnedNodeSimID == -1) {
            if (highlightedNode == null) {
                colorizeKeyboard(firstNode);
                pinnedNodeSimID = firstSimID;
                selectedNode = firstNode;
            } else {
                BigInteger highlightedChordID = (BigInteger) highlightedNode.getAttribute("chordId");
                BigInteger highlightNodeNextID = chordIDTreeMap.higherKey(highlightedChordID);
                decolorizeOld(highlightedNode);
                if (highlightNodeNextID != null) {
                    PNode nextNode = (PNode) getRelationships().get(chordIDTreeMap.get(highlightNodeNextID));
                    colorizeKeyboard(nextNode);
                    pinnedNodeSimID = chordIDTreeMap.get(highlightNodeNextID);
                    selectedNode = nextNode;
                } else {
                    colorizeKeyboard(firstNode);
                    pinnedNodeSimID = firstSimID;
                    selectedNode = firstNode;
                }
                highlightedNode = null;
            }
        } else {
            PNode pinnedNode = (PNode) getRelationships().get(pinnedNodeSimID);
            if(pinnedNode != null){
                decolorizeKeyboard(pinnedNode);
                BigInteger pinnedChordID = ((BigInteger) ((PNode) getRelationships().get(pinnedNodeSimID)).getAttribute("chordId"));
                BigInteger newChordID = chordIDTreeMap.higherKey(pinnedChordID);
                PNode nextNode;
                if (newChordID == null) {
                    nextNode = firstNode;
                    pinnedNodeSimID = firstSimID;
                } else {
                    nextNode = (PNode) getRelationships().get(chordIDTreeMap.get(newChordID));
                    pinnedNodeSimID = chordIDTreeMap.get(newChordID);
                }
                colorizeKeyboard(nextNode);
                selectedNode = nextNode;
                selected = true;
            } else {
                pinnedNodeSimID = firstSimID;
                colorizeKeyboard(firstNode);
                selectedNode = firstNode;
                selected = true;
            }
        }
    }

    private void selectPrevious() {
        BigInteger lastChordID = chordIDTreeMap.lastKey();
        Long lastSimID = chordIDTreeMap.get(lastChordID);
        PNode lastNode = (PNode) getRelationships().get(lastSimID);
        if (pinnedNodeSimID == -1) {
            if (highlightedNode == null) {
                colorizeKeyboard(lastNode);
                pinnedNodeSimID = lastSimID;
                selectedNode = lastNode;
            } else {
                BigInteger highlightedChordID = (BigInteger) highlightedNode.getAttribute("chordId");
                BigInteger highlightNodeNextID = chordIDTreeMap.higherKey(highlightedChordID);
                decolorizeOld(highlightedNode);
                if (highlightNodeNextID != null) {
                    PNode nextNode = (PNode) getRelationships().get(chordIDTreeMap.get(highlightNodeNextID));
                    colorizeKeyboard(nextNode);
                    pinnedNodeSimID = chordIDTreeMap.get(highlightNodeNextID);
                    selectedNode = nextNode;
                } else {
                    colorizeKeyboard(lastNode);
                    pinnedNodeSimID = lastSimID;
                    selectedNode = lastNode;
                }
                highlightedNode = null;
            }
        } else {
            PNode pinnedNode = (PNode) getRelationships().get(pinnedNodeSimID);
            if(pinnedNode != null){
                decolorizeKeyboard(pinnedNode);
                BigInteger pinnedChordID = ((BigInteger) ((PNode) getRelationships().get(pinnedNodeSimID)).getAttribute("chordId"));
                BigInteger newChordID = chordIDTreeMap.lowerKey(pinnedChordID);
                PNode nextNode;
                if (newChordID == null) {
                    nextNode = lastNode;
                    pinnedNodeSimID = lastSimID;
                } else {
                    nextNode = (PNode) getRelationships().get(chordIDTreeMap.get(newChordID));
                    pinnedNodeSimID = chordIDTreeMap.get(newChordID);
                }
                colorizeKeyboard(nextNode);
                selectedNode = nextNode;
                selected = true;
            } else {
                pinnedNodeSimID = lastSimID;
                colorizeKeyboard(lastNode);
                selectedNode = lastNode;
                selected = true;
            }
            
        }
    }

    private void colorizeNext(PNode nextNode) {
        PNode succNode, predNode;
        lines = new ArrayList();
        if (!nextNode.getAttribute("successor").equals("null")) {
            succNode = (PNode) getRelationships().get((Long) nextNode.getAttribute("successor"));
            succNode.setPaint(Color.RED);
            succNode.moveToFront();
            lines.add(drawLine(nextNode, succNode));
        } else {
            succNode = null;
        }

        if (!nextNode.getAttribute("predecessor").equals("null")) {
            predNode = (PNode) getRelationships().get((Long) nextNode.getAttribute("predecessor"));
            predNode.setPaint(Color.BLUE);
            predNode.moveToFront();
            lines.add(drawLine(nextNode, predNode));
        } else {
            predNode = null;
        }

        ArrayList fingerIDs = (ArrayList) nextNode.getAttribute("fingers");
        ArrayList fingerNodes = new ArrayList<>();
        for (int i = 0; i < fingerIDs.size(); i++) {
            if (!getRelationships().get((Long) fingerIDs.get(i)).equals("null")) {
                PNode finger = getRelationships().get((Long) fingerIDs.get(i));
                fingerNodes.add(finger);
                finger.setPaint(Color.ORANGE);
                finger.moveToFront();
                lines.add(drawCurvedLine(nextNode, finger, fingerIDs.size() + 1, i + 1));
            }
        }

        nextNode.setPaint(Color.GREEN);
        nextNode.moveToFront();

        edgeLayer.addChildren(lines);

        giveInfoToPanel(nextNode, succNode, predNode, fingerNodes);
    }

    private void decolorizeOld(PNode oldNode) {
        if (!oldNode.getAttribute("successor").equals("null")) {
            PNode succNode = (PNode) getRelationships().get((Long) oldNode.getAttribute("successor"));
            if (currentNetwork.getReason().equals("initial")) {
                succNode.setPaint(Color.WHITE);
                succNode.moveToBack();
            } else {
                if (diffHashSet.contains(succNode.getAttribute("simID"))) {
                    succNode.setPaint(Color.MAGENTA);
                    succNode.moveToFront();
                } else {
                    succNode.setPaint(Color.WHITE);
                    succNode.moveToBack();
                }
            }
        }

        if (!oldNode.getAttribute("predecessor").equals("null")) {
            PNode predNode = (PNode) getRelationships().get((Long) oldNode.getAttribute("predecessor"));
            if (currentNetwork.getReason().equals("initial")) {
                predNode.setPaint(Color.WHITE);
                predNode.moveToBack();
            } else {
                if (diffHashSet.contains(predNode.getAttribute("simID"))) {
                    predNode.setPaint(Color.MAGENTA);
                    predNode.moveToFront();
                } else {
                    predNode.setPaint(Color.WHITE);
                    predNode.moveToBack();
                }
            }
        }

        ArrayList fingerIDs = (ArrayList) oldNode.getAttribute("fingers");
        for (int i = 0; i < fingerIDs.size(); i++) {
            if (!getRelationships().get((Long) fingerIDs.get(i)).equals("null")) {
                PNode finger = getRelationships().get((Long) fingerIDs.get(i));
                if (currentNetwork.getReason().equals("initial")) {
                    finger.setPaint(Color.WHITE);
                    finger.moveToBack();
                } else {
                    if (diffHashSet.contains(finger.getAttribute("simID"))) {
                        finger.setPaint(Color.MAGENTA);
                        finger.moveToFront();
                    } else {
                        finger.setPaint(Color.WHITE);
                        finger.moveToBack();
                    }
                }
            }
        }

        if (currentNetwork.getReason().equals("initial")) {
            oldNode.setPaint(Color.WHITE);
            oldNode.moveToBack();
        } else {
            if (diffHashSet.contains(oldNode.getAttribute("simID"))) {
                oldNode.setPaint(Color.MAGENTA);
                oldNode.moveToFront();
            } else {
                oldNode.setPaint(Color.WHITE);
                oldNode.moveToBack();
            }
        }

        edgeLayer.removeChildren(lines);

        int size = lines.size();
        for (int i = 0; i < size; i++) {
            lines.remove(0);
        }

        removeInfoFromPanel();
    }

    private void colorizeKeyboard(PNode aNode) {
        if(aNode != null){
            colorizeNext(aNode);
            mouseFilter.setAcceptsMouseExited(false);
            mouseFilter.setAcceptsMouseEntered(false);
        }
    }

    private void decolorizeKeyboard(PNode aNode) {
        if(aNode != null){
            decolorizeOld(aNode);
            mouseFilter.setAcceptsMouseExited(true);
            mouseFilter.setAcceptsMouseEntered(true);
        }
    }

    private class Circle extends PPath {

        Circle(double angle, boolean isNew) {
            setStrokePaint(Color.black);
            setStroke(new PFixedWidthStroke());
            if (isNew) {
                setPaint(Color.MAGENTA);
            } else {
                setPaint(Color.white);
            }
            float x = cx + (float) (radius * Math.sin(angle));
            float y = cy - (float) (radius * Math.cos(angle));
            setPathToEllipse(x, y, 9, 9);
        }
    }

    public class MouseEventHandler extends PBasicInputEventHandler {

        PInputEventFilter eventFilter;

        public MouseEventHandler(PInputEventFilter mouseFilter) {
            eventFilter = mouseFilter;
            eventFilter.setOrMask(InputEvent.BUTTON1_MASK);
            setEventFilter(eventFilter);
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            if (e.getButton() == MouseEvent.NOBUTTON) {
                colorizeNext(e.getPickedNode());
                highlightedNode = e.getPickedNode();
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            decolorizeOld(e.getPickedNode());
            highlightedNode = null;
        }

        @Override
        public void mouseClicked(PInputEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (pinnedNodeSimID == -1) {
                    decolorizeKeyboard(e.getPickedNode());
                    colorizeKeyboard(e.getPickedNode());
                    pinnedNodeSimID = (long) e.getPickedNode().getAttribute("simID");
                } else {
                    if (pinnedNodeSimID == (long) e.getPickedNode().getAttribute("simID")) {
                        decolorizeKeyboard(e.getPickedNode());
                        pinnedNodeSimID = -1;
                    } else {
                        PNode oldNode = getRelationships().get(pinnedNodeSimID);
                        decolorizeKeyboard(oldNode);
                        colorizeKeyboard(e.getPickedNode());
                        pinnedNodeSimID = (long) e.getPickedNode().getAttribute("simID");
                    }
                }
            }
        }
    }

    private class TooltipHandler extends PBasicInputEventHandler {

        public TooltipHandler() {
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            PNode node = e.getPickedNode();
            selectedTooltipNode.setText(tooltipText(node));
            selectedTooltipNode.setVisible(true);
        }

        @Override
        public void mouseExited(PInputEvent e) {
            selectedTooltipNode.setVisible(false);
        }
    }

    private class StepUpdater implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            stepUpdate(de);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            stepUpdate(de);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void stepUpdate(DocumentEvent de) {
            try {
                step = Integer.parseInt(stepField.getText());
            } catch (NumberFormatException nme) {
                step = 0;
            }
        }
    }

    private class GotoUpdater implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            gotoUpdate(de);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            gotoUpdate(de);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void gotoUpdate(DocumentEvent de) {
            int destination;
            try {
                destination = Integer.parseInt(gotoField.getText());
            } catch (NumberFormatException nme) {
                destination = 0;
            }
            drawGoto(destination);
        }
    }
}
