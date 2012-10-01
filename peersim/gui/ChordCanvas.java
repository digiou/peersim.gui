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
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author jim
 */
public class ChordCanvas extends PCanvas {

    private Dimension SCREEN = new Dimension(1024, 768);
    private int historySize = NetworkHistory.getSize() - 1;
    private int margin = 50;
    private int chordPosition;
    private int width = SCREEN.width;
    private int height = SCREEN.height;
    private int radius = width / 2;
    private int current = 0;
    private int step = 1;
    private float cx = margin + radius;
    private float cy = margin + radius;
    private long pinnedNodeSimID = -1;
    private PCamera camera;
    private Point2D epicenter = new Point2D.Double(cx, cy);
    private Hashtable<Long, PNode> hashTable;
    private PLayer nodeLayer = this.getLayer();
    private PLayer edgeLayer = new PLayer();
    private InfoPanel panel;
    private HistoryObject currentNetwork;
    private JButton back, frwrd;
    private JTextField stepField;
    private PBasicInputEventHandler colors, tooltip;
    private PText eventTooltipNode;

    public ChordCanvas(InfoPanel inheritedPanel, JButton back, final JButton frwrd, JTextField stepField) {
        super();
        setSize(SCREEN);
        this.panel = inheritedPanel;
        this.back = back;
        this.frwrd = frwrd;
        this.stepField = stepField;

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

        draw(current);
        
        this.stepField.getDocument().addDocumentListener(new stepDocumentListener());
        frwrd.setText("Next 1 event");
        back.setText("Back 1 event");

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
        frwrd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "drawNext");
        frwrd.getActionMap().put("drawNext", drawNext);

        Action drawPrevious = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPrevious(step);
            }
        };
        back.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "drawPrevious");
        back.getActionMap().put("drawPrevious", drawPrevious);

        this.setVisible(true);
    }

    private Hashtable<Long, PNode> getRelationships() {
        return hashTable;
    }

    private void draw(int pointer) {
        currentNetwork = NetworkHistory.getEntry(pointer);
        eventTooltipNode.setText("Current event: " + currentNetwork.getReason() + " @time: " + currentNetwork.getTime());
        hashTable = new Hashtable<Long, PNode>(currentNetwork.size());
        drawNodes(nodeLayer);
        colors = new ChordMouseEventHandler();
        tooltip = new TooltipHandler();
        nodeLayer.addInputEventListener(colors);
        nodeLayer.addInputEventListener(tooltip);
    }

    private void drawNodes(PLayer nodeLayer) {
        for (int i = 0; i < currentNetwork.size(); i++) {
            HistoryChordProtocol hcp = (HistoryChordProtocol) currentNetwork.getNode(i).getProtocol();
            double angle = getAngle(hcp);
            Circle node = new Circle(angle);
            nodeLayer.addChild(node);
            storeInfo(node, hcp, currentNetwork.getNode(i).getID());
            hashTable.put(currentNetwork.getNode(i).getID(), node);
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

        if (arraylist.get(0) != null) {
            panel.addFingersToPanel(arraylist);
        } else {
            panel.setNullFingers();
        }

    }

    private void removeInfoFromPanel() {
        panel.resetPanel();
    }

    private void clearCanvas() {
        edgeLayer.removeAllChildren();
        nodeLayer.removeAllChildren();
        nodeLayer.removeInputEventListener(colors);
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
    
    private class Circle extends PPath{
        Circle(double angle){
            setPaint(Color.white);
            setStrokePaint(Color.black);
            setStroke(new PFixedWidthStroke());
            float x = cx + (float) (radius * Math.sin(angle));
            float y = cy - (float) (radius * Math.cos(angle));
            setPathToEllipse(x, y, 10, 10);
        }
    }

    public class ChordMouseEventHandler extends PBasicInputEventHandler {

        PInputEventFilter filter;
        ArrayList lines = new ArrayList();
        Boolean selectedSomething = true;
        PNode something;
        PNode pred, succ;
        ArrayList fingerNodes;

        public ChordMouseEventHandler() {
            filter = new PInputEventFilter();
            filter.setOrMask(InputEvent.BUTTON1_MASK);
            if (pinnedNodeSimID != -1) {
                if (getRelationships().get(pinnedNodeSimID) != null) {
                    selectedSomething = false;
                    filter.setAcceptsMouseEntered(false);
                    filter.setAcceptsMouseExited(false);
                    something = (PNode) getRelationships().get(pinnedNodeSimID);
                    if (!something.getAttribute("predecessor").equals("null")) {
                        pred = (PNode) getRelationships().get((Long) something.getAttribute("predecessor"));
                        if (pred != null) {
                            lines.add(drawLine(something, pred));
                            pred.setPaint(Color.RED);
                            pred.moveToFront();
                        }
                    } else {
                        pred = null;
                    }

                    if (!something.getAttribute("successor").equals("null")) {
                        succ = (PNode) getRelationships().get((Long) something.getAttribute("successor"));
                        if (succ != null) {
                            lines.add(drawLine(something, succ));
                            succ.setPaint(Color.BLUE);
                            succ.moveToFront();
                        }
                    } else {
                        succ = null;
                    }

                    ArrayList fingerID = (ArrayList) something.getAttribute("fingers");
                    fingerNodes = new ArrayList();
                    for (int i = 0; i < fingerID.size(); i++) {
                        if (getRelationships().get((Long) fingerID.get(i)) != null) {
                            fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                        }
                    }
                    int size = fingerNodes.size();
                    for (int i = 0; i < size; i++) {
                        ((PNode) fingerNodes.get(i)).setPaint(Color.YELLOW);
                        ((PNode) fingerNodes.get(i)).moveToFront();
                        lines.add(drawCurvedLine(something, (PNode) fingerNodes.get(i), size+1, i+1));
                    }

                    something.setPaint(Color.GREEN);

                    something.moveToFront();
                    edgeLayer.addChildren(lines);
                    giveInfoToPanel(something, succ, pred, fingerNodes);
                } else {
                    pinnedNodeSimID = -1;
                }
            }
            setEventFilter(filter);
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            super.mouseEntered(e);
            if (e.getButton() == MouseEvent.NOBUTTON) {
                //special pred treatment
                if (!e.getPickedNode().getAttribute("predecessor").equals("null")) {
                    pred = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("predecessor"));
                    //necessary
                    if (pred != null) {
                        lines.add(drawLine(e.getPickedNode(), pred));
                        pred.setPaint(Color.RED);
                        pred.moveToFront();
                    }
                } else {
                    pred = null;
                }

                if (!e.getPickedNode().getAttribute("successor").equals("null")) {
                    succ = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("successor"));
                    if (succ != null) {
                        lines.add(drawLine(e.getPickedNode(), succ));
                        succ.setPaint(Color.BLUE);
                        succ.moveToFront();
                    }
                } else {
                    succ = null;
                }


                ArrayList fingerID = (ArrayList) e.getPickedNode().getAttribute("fingers");
                fingerNodes = new ArrayList();
                for (int i = 0; i < fingerID.size(); i++) {
                    if (getRelationships().get((Long) fingerID.get(i)) != null) {
                        fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                    }
                }
                int size = fingerNodes.size();
                for (int i = 0; i < size; i++) {
                    ((PNode) fingerNodes.get(i)).setPaint(Color.YELLOW);
                    ((PNode) fingerNodes.get(i)).moveToFront();
                    lines.add(drawCurvedLine(e.getPickedNode(), (PNode) fingerNodes.get(i), size+1, i+1));
                }

                e.getPickedNode().setPaint(Color.GREEN);
                e.getPickedNode().moveToFront();
                edgeLayer.addChildren(lines);
            }
        }

        @Override
        public void mouseClicked(PInputEvent e) {
            super.mouseClicked(e);
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (selectedSomething) {
                    something = e.getPickedNode();
                    pinnedNodeSimID = (long) e.getPickedNode().getAttribute("simID");
                    filter.setAcceptsMouseExited(false);
                    filter.setAcceptsMouseEntered(false);
                    selectedSomething = false;
                    giveInfoToPanel(e.getPickedNode(), succ, pred, fingerNodes);
                } else {
                    if (something.equals(e.getPickedNode()) || pinnedNodeSimID == (long) e.getPickedNode().getAttribute("simID")) {
                        pinnedNodeSimID = -1;
                        filter.setAcceptsMouseExited(true);
                        filter.setAcceptsMouseEntered(true);
                        selectedSomething = true;

                        e.getPickedNode().setPaint(Color.WHITE);
                        if (pred != null) {
                            pred.setPaint(Color.WHITE);
                            pred.moveToBack();
                        }

                        if (succ != null) {
                            succ.setPaint(Color.WHITE);
                            succ.moveToBack();
                        }

                        e.getPickedNode().moveToBack();

                        edgeLayer.removeChildren(lines);

                        int fingersSize = fingerNodes.size();
                        for (int i = 0; i < fingersSize; i++) {
                            if ((PNode) fingerNodes.get(i) != null) {
                                ((PNode) fingerNodes.get(i)).setPaint(Color.WHITE);
                                ((PNode) fingerNodes.get(i)).moveToBack();
                            }
                        }

                        int linesSize = lines.size();
                        for (int i = 0; i < linesSize; i++) {
                            lines.remove(0);
                        }
                        removeInfoFromPanel();
                    } else {
                        something.setPaint(Color.WHITE);
                        if (pred != null) {
                            pred.setPaint(Color.WHITE);
                            pred.moveToBack();
                        }

                        if (succ != null) {
                            succ.setPaint(Color.WHITE);
                            succ.moveToBack();
                        }

                        something.moveToBack();
                        edgeLayer.removeChildren(lines);

                        int fingersSize = fingerNodes.size();
                        for (int i = 0; i < fingersSize; i++) {
                            if ((PNode) fingerNodes.get(i) != null) {
                                ((PNode) fingerNodes.get(i)).setPaint(Color.WHITE);
                                ((PNode) fingerNodes.get(i)).moveToBack();
                            }
                        }

                        int linesSize = lines.size();
                        for (int i = 0; i < linesSize; i++) {
                            lines.remove(0);
                        }

                        selectedSomething = false;

                        if (!e.getPickedNode().getAttribute("predecessor").equals("null")) {
                            pred = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("predecessor"));
                            if (pred != null) {
                                lines.add(drawLine(e.getPickedNode(), pred));
                                pred.setPaint(Color.RED);
                                pred.moveToFront();
                            }
                        } else {
                            pred = null;
                        }

                        if (!e.getPickedNode().getAttribute("successor").equals("null")) {
                            succ = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("successor"));
                            if (succ != null) {
                                lines.add(drawLine(e.getPickedNode(), succ));
                                succ.setPaint(Color.BLUE);
                                succ.moveToFront();
                            }
                        } else {
                            succ = null;
                        }

                        ArrayList fingerID = (ArrayList) e.getPickedNode().getAttribute("fingers");
                        fingerNodes = new ArrayList();
                        for (int i = 0; i < fingerID.size(); i++) {
                            if (getRelationships().get((Long) fingerID.get(i)) != null) {
                                fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                            }
                        }
                        int size = fingerNodes.size();
                        for (int i = 0; i < size; i++) {
                            ((PNode) fingerNodes.get(i)).setPaint(Color.YELLOW);
                            ((PNode) fingerNodes.get(i)).moveToFront();
                            lines.add(drawCurvedLine(e.getPickedNode(), (PNode) fingerNodes.get(i), size+1, i+1));
                        }

                        e.getPickedNode().setPaint(Color.GREEN);

                        e.getPickedNode().moveToFront();
                        edgeLayer.addChildren(lines);

                        something = e.getPickedNode();
                        pinnedNodeSimID = (long) e.getPickedNode().getAttribute("simID");
                        giveInfoToPanel(e.getPickedNode(), succ, pred, fingerNodes);
                    }
                }
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            super.mouseExited(e);
            if (e.getButton() == MouseEvent.NOBUTTON) {
                e.getPickedNode().setPaint(Color.WHITE);
                if (pred != null) {
                    pred.setPaint(Color.WHITE);
                    pred.moveToBack();
                }

                if (succ != null) {
                    succ.setPaint(Color.WHITE);
                    succ.moveToBack();
                }

                e.getPickedNode().moveToBack();

                edgeLayer.removeChildren(lines);

                int fingersSize = fingerNodes.size();
                for (int i = 0; i < fingersSize; i++) {
                    if (fingerNodes.get(i) != null) {
                        ((PNode) fingerNodes.get(i)).setPaint(Color.WHITE);
                        ((PNode) fingerNodes.get(i)).moveToBack();
                    }
                }

                int linesSize = lines.size();
                for (int i = 0; i < linesSize; i++) {
                    lines.remove(0);
                }
            }
        }
    }

    private class TooltipHandler extends PBasicInputEventHandler {

        final PText tooltipNode = new PText();

        public TooltipHandler() {
            tooltipNode.setPickable(false);
            camera.addChild(tooltipNode);
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            PNode node = e.getPickedNode();
            tooltipNode.setText(tooltipText(node));
            tooltipNode.setVisible(true);
        }

        @Override
        public void mouseMoved(PInputEvent e) {
            if (tooltipNode.getVisible()) {
                PNode picked = e.getPickedNode();
                Point2D point = e.getPositionRelativeTo(picked);
                picked.localToParent(point);
                point.setLocation(0, 15);
                tooltipNode.setOffset(point);
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            tooltipNode.setVisible(false);
        }
    }

    private class stepDocumentListener implements DocumentListener {

        String content;

        @Override
        public void insertUpdate(DocumentEvent de) {
            try {
                content = de.getDocument().getText(0, de.getDocument().getLength());
                step = Integer.parseInt(content);
                if (step == 1) {
                    frwrd.setText("Next " + step + " event");
                    back.setText("Back " + step + " event");
                } else {
                    frwrd.setText("Next " + step + " events");
                    back.setText("Back " + step + " events");
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(ChordCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            try {
                content = de.getDocument().getText(0, de.getDocument().getLength());
                if (content.equals("") || content.equals("1")) {
                    step = 1;
                    frwrd.setText("Next 1 event");
                    back.setText("Back 1 event");
                } else {
                    step = Integer.parseInt(content);
                    frwrd.setText("Next " + step + " events");
                    back.setText("Back " + step + " events");
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(ChordCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
