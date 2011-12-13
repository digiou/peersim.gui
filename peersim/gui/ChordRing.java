package peersim.gui;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import peersim.chord.ChordProtocol;
import peersim.core.Linkable;
import peersim.core.Network;

/**
 *
 * @author jim
 */
public class ChordRing extends PFrame {

    private static final Dimension SCREEN = new Dimension(1024, 768);
    private static final int NODES = Network.size();
    private PCamera camera;
    int margin = 50;
    int width = SCREEN.width;
    int height = SCREEN.height;
    int radius = width / 2;
    float cx = margin + radius;
    float cy = margin + radius;
    Hashtable<Long, PNode> hashTable = new Hashtable<Long, PNode>(NODES);
    final PCanvas canvas = getCanvas();
    PLayer nodeLayer = canvas.getLayer();

    public static void main(String[] args) {
        new ChordRing();
    }

    public ChordRing() {
        super("Ring", false, new PSwingCanvas());
    }

    @Override
    public void initialize() {
        setSize(SCREEN);

        camera = canvas.getCamera();
        PLayer edgeLayer = new PLayer();
        canvas.getRoot().addChild(edgeLayer);
        camera.addLayer(0, edgeLayer);

        drawNodes(nodeLayer);
        /*Point2D center = new Point2D.Double((float) (SCREEN.width / 2), (float) SCREEN.height);
        ArrayList<Point2D> circlePoints = circlePoints(center, (float) SCREEN.height / 2);
        
        for (int i = 0;i < NODES;i++) {
        Point2D point = circlePoints.get(i);
        PPath node = PPath.createEllipse((float) point.getX(), (float) point.getY(), 16, 16);
        node.addAttribute("edges", new ArrayList());
        node.addAttribute("prevPeers", new ArrayList());
        node.addAttribute("nextPeers", new ArrayList());
        nodeLayer.addChild(node);
        }
        
        for (int i = 0;i < NODES;i++) {
        int n1 = i;
        Linkable p = (Linkable) Network.get(i).getProtocol(1);
        for (int j = 0; j < p.degree(); j++) {
        int n2 = p.getNeighbor(j).getIndex();
        
        PNode node1 = nodeLayer.getChild(n1);
        PNode node2 = nodeLayer.getChild(n2);
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        ((ArrayList) node1.getAttribute("nextPeers")).add(node2);
        ((ArrayList) node2.getAttribute("prevPeers")).add(node1);
        PPath edge = new PPath(createArrow(new Point2D.Double(start.getX(), start.getY()),
        new Point2D.Double(end.getX(), end.getY())));
        
        
        ((ArrayList) node1.getAttribute("edges")).add(edge);
        ((ArrayList) node2.getAttribute("edges")).add(edge);
        edge.addAttribute("nodes", new ArrayList());
        ((ArrayList) edge.getAttribute("nodes")).add(node1);
        ((ArrayList) edge.getAttribute("nodes")).add(node2);
        edge.setVisible(false);
        edgeLayer.addChild(edge);
        }
        }*/

        nodeLayer.addInputEventListener(new ChordMouseEventHandler()
                /*new PBasicInputEventHandler() {

                    {
                        PInputEventFilter filter = new PInputEventFilter();
                        filter.setOrMask(InputEvent.BUTTON1_MASK);
                        setEventFilter(filter);
                    }

                    @Override
                    public void mouseEntered(PInputEvent e) {
                        super.mouseEntered(e);
                        if (e.getButton() == MouseEvent.NOBUTTON) {
                            ArrayList fingers = (ArrayList) e.getPickedNode().getAttribute("fingers");
                            PNode pred = (PNode) getNodeLayer().getChild(
                                    getRelationships().get(
                                    (Long) e.getPickedNode().getAttribute("predecessor")));
                            PNode succ = (PNode) getNodeLayer().getChild(
                                    getRelationships().get(
                                    (Long) e.getPickedNode().getAttribute("successor")));
                            e.getPickedNode().setPaint(Color.GREEN);
                            e.getPickedNode().moveToFront();
                            succ.moveToFront();
                            pred.moveToFront();

                            pred.setPaint(Color.RED);
                            succ.setPaint(Color.BLUE);
                            /*e.getPickedNode().moveToFront();
                            ArrayList prevPeers = (ArrayList) e.getPickedNode().getAttribute("prevPeers");
                            ArrayList nextPeers = (ArrayList) e.getPickedNode().getAttribute("nextPeers");
                            ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
                            for (int i = 0; i < prevPeers.size(); i++) {
                            PNode prevNode = (PNode) prevPeers.get(i);
                            prevNode.setPaint(Color.RED);
                            prevNode.moveToFront();
                            }
                            for (int i = 0; i < nextPeers.size(); i++) {
                            PNode nextNode = (PNode) nextPeers.get(i);
                            nextNode.setPaint(Color.BLUE);
                            nextNode.moveToFront();
                            }
                            for (int i = 0; i < edges.size(); i++) {
                            PPath edge = (PPath) edges.get(i);
                            edge.setVisible(true);
                            }
                        }
                    }

                    @Override
                    public void mouseExited(PInputEvent e) {
                        super.mouseExited(e);
                        if (e.getButton() == MouseEvent.NOBUTTON) {
                            ArrayList fingers = (ArrayList) e.getPickedNode().getAttribute("fingers");
                            PNode pred = (PNode) getNodeLayer().getChild(
                                    getRelationships().get(
                                    (Long) e.getPickedNode().getAttribute("predecessor")));
                            PNode succ = (PNode) getNodeLayer().getChild(
                                    getRelationships().get(
                                    (Long) e.getPickedNode().getAttribute("successor")));

                            e.getPickedNode().setPaint(Color.WHITE);
                            e.getPickedNode().moveToBack();
                            pred.setPaint(Color.WHITE);
                            succ.setPaint(Color.WHITE);
                            /*e.getPickedNode().moveToBack();
                            ArrayList prevPeers = (ArrayList) e.getPickedNode().getAttribute("prevPeers");
                            ArrayList nextPeers = (ArrayList) e.getPickedNode().getAttribute("nextPeers");
                            ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
                            for (int i = 0; i < prevPeers.size(); i++) {
                            PNode prevNode = (PNode) prevPeers.get(i);
                            prevNode.setPaint(Color.WHITE);
                            prevNode.moveToBack();
                            }
                            for (int i = 0; i < nextPeers.size(); i++) {
                            PNode nextNode = (PNode) nextPeers.get(i);
                            nextNode.setPaint(Color.WHITE);
                            nextNode.moveToBack();
                            }
                            for (int i = 0; i < edges.size(); i++) {
                            PPath edge = (PPath) edges.get(i);
                            edge.setVisible(false);
                            }
                        }
                    }
                }*/);
    }

    private Hashtable<Long, PNode> getRelationships() {
        return hashTable;
    }

    private void drawNodes(PLayer nodeLayer) {
        for (int i = 0; i < Network.size(); i++) {
            ChordProtocol cp = (ChordProtocol) Network.get(i).getProtocol(0);
            double angle = getAngle(cp);
            PPath node = nodePosition(angle);
            nodeLayer.addChild(node);
            storeInfo(node, cp);
            hashTable.put(Network.get(i).getID(), node);
        }
    }

    private void storeInfo(PNode node, ChordProtocol cp) {
        node.addAttribute("drawnLines", false);
        node.addAttribute("lines", new ArrayList());
        node.addAttribute("predecessor", cp.predecessor.getID());
        node.addAttribute("successor", cp.successorList[0].getID());
        node.addAttribute("fingers", new ArrayList());
        int SIZE = cp.fingerTable.length;
        for (int i = 0; i < SIZE; i++) {
            ((ArrayList) node.getAttribute("fingers")).add(cp.fingerTable[i].getID());
        }
    }

    private PPath nodePosition(double angle) {
        float x = cx - 5 + (float) (radius * Math.sin(angle));
        float y = cy - 5 - (float) (radius * Math.cos(angle));
        return PPath.createEllipse(x, y, 10, 10);
    }

    private double getAngle(ChordProtocol cp) {
        double chordId = cp.chordId.doubleValue();
        double maxValue = Math.pow(2, cp.m);
        double degree = ((chordId / maxValue) * 365);
        double radian = degree / 180 * Math.PI;
        return radian;
    }
    
    private void lineVisibility(PNode node, Boolean state){
        ArrayList lines = (ArrayList) node.getAttribute("lines");
        int numOfLines = lines.size();
        for(int i = 0; i < numOfLines; i++){
            ((PPath) lines.get(i)).setVisible(state);
        }
    }
    
    private void drawLines(PNode node){
        drawLine(node, (PNode)node.getAttribute("predecessor"));
        drawLine(node, (PNode)node.getAttribute("successor"));
    }
    
    private void drawLine(PNode start, PNode end){
        
    }

    private Shape createArrow(Point2D start, Point2D end) {

        // Arrow settings.
        int b = 20;
        double theta = Math.toRadians(17);

        // Arrow Calculations.
        double xs = start.getX();
        double ys = start.getY();
        double xe = end.getX();
        double ye = end.getY();
        double alpha = Math.atan2(ye - ys, xe - xs);
        double dx1 = b * Math.cos(alpha + theta);
        double dy1 = b * Math.sin(alpha + theta);
        double dx2 = b * Math.cos(alpha - theta);
        double dy2 = b * Math.sin(alpha - theta);

        // Arrow Path.
        GeneralPath path = new GeneralPath();
        path.moveTo(xs, ys);
        path.lineTo(xe, ye);
        path.lineTo(xe - dx1, ye - dy1);
        path.moveTo(xe, ye);
        path.lineTo(xe - dx2, ye - dy2);

        return path;
    }

    public class ChordMouseEventHandler extends PBasicInputEventHandler {

        PInputEventFilter filter = new PInputEventFilter();
        PNode pred;
        PNode succ;
        ArrayList fingers;
        public ChordMouseEventHandler() {
            filter.setOrMask(InputEvent.BUTTON1_MASK);
            setEventFilter(filter);
        }

        @Override
        public void mouseEntered(PInputEvent e) {
            super.mouseEntered(e);
            if (e.getButton() == MouseEvent.NOBUTTON) {
                fingers = (ArrayList) e.getPickedNode().getAttribute("fingers");
                pred = (PNode) getRelationships().get((Long)e.getPickedNode().getAttribute("predecessor"));
                succ = (PNode) getRelationships().get((Long)e.getPickedNode().getAttribute("successor"));
                e.getPickedNode().setPaint(Color.GREEN);
                pred.setPaint(Color.RED);
                succ.setPaint(Color.BLUE);
                e.getPickedNode().moveToFront();
                succ.moveToFront();
                pred.moveToFront();
                if((Boolean)e.getPickedNode().getAttribute("drawnLines")){
                    lineVisibility(e.getPickedNode(), true);
                } else {
                    
                }
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            super.mouseExited(e);
            if (e.getButton() == MouseEvent.NOBUTTON) {
                e.getPickedNode().setPaint(Color.WHITE);
                pred.setPaint(Color.WHITE);
                succ.setPaint(Color.WHITE);
                e.getPickedNode().moveToFront();
                succ.moveToBack();
                pred.moveToBack();
                lineVisibility(e.getPickedNode(), false);
            }
        }
    }
}
