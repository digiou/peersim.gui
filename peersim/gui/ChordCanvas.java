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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JButton;
import peersim.chord.ChordProtocol;
import peersim.core.Node;

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
    private float cx = margin + radius;
    private float cy = margin + radius;
    private PCamera camera;
    private Point2D epicenter = new Point2D.Double(cx, cy);
    private Hashtable<Long, PNode> hashTable;
    private PLayer nodeLayer = this.getLayer();
    private PLayer edgeLayer = new PLayer();
    private InfoPanel panel;
    private int current = 0;
    private HistoryObject currentNetwork;
    private JButton back, frwrd;
    private PBasicInputEventHandler colors, tooltip;
    private int counter = 0;

    public ChordCanvas(InfoPanel inheritedPanel, JButton back, final JButton frwrd) {
        super();
        setSize(SCREEN);
        this.panel = inheritedPanel;
        this.back = back;
        this.frwrd = frwrd;

        this.back.setEnabled(false);
        if (historySize == 0) {
            this.frwrd.setEnabled(false);
        }

        camera = this.getCamera();
        this.getRoot().addChild(edgeLayer);
        camera.addLayer(0, edgeLayer);

        draw(current);

        frwrd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawNext();
            }
        });
        back.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawPrevious();
            }
        });

        this.setVisible(true);
    }

    /*
     * @Override public void initialize() { setSize(SCREEN); camera =
     * canvas.getCamera(); canvas.getRoot().addChild(edgeLayer);
     * camera.addLayer(0, edgeLayer);
     *
     * findChordProtocol(); drawNodes(nodeLayer);
     *
     * nodeLayer.addInputEventListener(new ChordMouseEventHandler());
     * nodeLayer.addInputEventListener(new TooltipHandler()); }
     */
    private Hashtable<Long, PNode> getRelationships() {
        return hashTable;
    }

    private void findChordProtocol(Node aNode) {
        for (int i = 0; i < aNode.protocolSize(); i++) {
            if (aNode.getProtocol(i).getClass() == ChordProtocol.class) {
                chordPosition = i;
                break;
            }
        }
    }

    private void draw(int pointer) {
        currentNetwork = NetworkHistory.getEntry(pointer);
        hashTable = new Hashtable<Long, PNode>(currentNetwork.size());
        //findChordProtocol(currentNetwork.getNode(0));
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
            PPath node = nodePosition(angle);
            node.setStroke(new PFixedWidthStroke());
            nodeLayer.addChild(node);
            storeInfo(node, hcp, currentNetwork.getNode(i).getID());
            hashTable.put(currentNetwork.getNode(i).getID(), node);
        }
    }

    private void storeInfo(PNode node, HistoryChordProtocol hcp, long SimID) {
        if (hcp.predecessor.equals(SimID)) {
            node.addAttribute("predecessor", "null");/*
             * node just added, I put the node's /*simulation ID if predecessor
             * is not yet found by ChordProtocol controls
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
        for (int i = 0; i < SIZE; i++) {
            if (!(hcp.fingerTable[i] == succId || hcp.fingerTable[i] == SimID)) {
                ((ArrayList) node.getAttribute("fingers")).add(hcp.fingerTable[i]);
            }
        }
        node.addAttribute("chordId", hcp.chordId);
    }

    private PPath nodePosition(double angle) {
        float x = cx + (float) (radius * Math.sin(angle));
        float y = cy - (float) (radius * Math.cos(angle));
        return PPath.createEllipse(x, y, 10, 10);
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

    private PPath drawCurvedLine(PNode start, PNode end) {
        Point2D centerStart = start.getFullBoundsReference().getCenter2D();
        Point2D centerEnd = end.getFullBoundsReference().getCenter2D();
        int i = 0;
        Point2D midpoint = midpoint(centerStart, centerEnd);
        Point2D newPoint = pointBetween(midpoint, epicenter);
        PPath line = new PPath();
        line.moveTo((float) centerStart.getX(), (float) centerStart.getY());
        line.curveTo((float) newPoint.getX(), (float) newPoint.getY(), (float) newPoint.getX(), (float) newPoint.getY(), (float) centerEnd.getX(), (float) centerEnd.getY());
        line.setStroke(new PFixedWidthStroke());
        return line;
    }

    private Point2D midpoint(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 2,
                p1.getY() + (p2.getY() - p1.getY()) / 2);
    }

    private Point2D pointBetween(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 17,
                p1.getY() + (p2.getY() - p1.getY()) / 17);
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

    private void drawNext() {
        clearCanvas();
        current++;
        draw(current);
        if (current == 1) {
            back.setEnabled(true);
        }
        if (current == historySize) {
            frwrd.setEnabled(false);
        }
    }

    private void drawPrevious() {
        clearCanvas();
        current--;
        draw(current);
        if (current == 0) {
            back.setEnabled(false);
        }
        if (current == historySize - 1) {
            frwrd.setEnabled(true);
        }
    }

    public class ChordMouseEventHandler extends PBasicInputEventHandler {

        PInputEventFilter filter = new PInputEventFilter();
        ArrayList lines = new ArrayList();
        Boolean selectedSomething = true;
        PNode something;
        PNode pred, succ;
        ArrayList fingerNodes;

        public ChordMouseEventHandler() {
            filter.setOrMask(InputEvent.BUTTON1_MASK);
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
                    fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                    if (((PNode) fingerNodes.get(i)) != null) {
                        ((PNode) fingerNodes.get(i)).setPaint(Color.YELLOW);
                        ((PNode) fingerNodes.get(i)).moveToFront();
                        lines.add(drawCurvedLine(e.getPickedNode(), (PNode) fingerNodes.get(i)));
                    }

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
                    filter.setAcceptsMouseExited(false);
                    filter.setAcceptsMouseEntered(false);
                    selectedSomething = false;
                    giveInfoToPanel(e.getPickedNode(), succ, pred, fingerNodes);
                } else {
                    if (something.equals(e.getPickedNode())) {
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
                            ((PNode) fingerNodes.get(i)).setPaint(Color.WHITE);
                            ((PNode) fingerNodes.get(i)).moveToBack();
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
                            ((PNode) fingerNodes.get(i)).setPaint(Color.WHITE);
                            ((PNode) fingerNodes.get(i)).moveToBack();
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
                            fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                            if (((PNode) fingerNodes.get(i)) != null) {
                                ((PNode) fingerNodes.get(i)).setPaint(Color.YELLOW);
                                ((PNode) fingerNodes.get(i)).moveToFront();
                                lines.add(drawCurvedLine(e.getPickedNode(), (PNode) fingerNodes.get(i)));
                            }
                        }

                        e.getPickedNode().setPaint(Color.GREEN);

                        e.getPickedNode().moveToFront();
                        edgeLayer.addChildren(lines);

                        something = e.getPickedNode();
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
                point.setLocation(10, 15);
                tooltipNode.setOffset(point);
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            tooltipNode.setVisible(false);
        }
    }
}
