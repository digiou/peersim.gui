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
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import peersim.chord.ChordProtocol;
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
    int chordPosition;
    int width = SCREEN.width;
    int height = SCREEN.height;
    int radius = width / 2;
    float cx = margin + radius;
    float cy = margin + radius;
    Hashtable<Long, PNode> hashTable = new Hashtable<Long, PNode>(NODES);
    final PCanvas canvas = getCanvas();
    PLayer nodeLayer = canvas.getLayer();
    PLayer edgeLayer = new PLayer();

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
        canvas.getRoot().addChild(edgeLayer);
        camera.addLayer(0, edgeLayer);
        
        findChordProtocol();
        drawNodes(nodeLayer);
        nodeLayer.addInputEventListener(new ChordMouseEventHandler());
        nodeLayer.addInputEventListener(new TooltipHandler());
    }

    private Hashtable<Long, PNode> getRelationships() {
        return hashTable;
    }
    
    private void findChordProtocol(){
        for(int i=0;i<Network.get(0).protocolSize();i++){
            if(Network.get(0).getProtocol(i).getClass() == ChordProtocol.class){
                chordPosition = i;
                break;
            }
        }
    }

    private void drawNodes(PLayer nodeLayer) {
        for (int i = 0; i < Network.size(); i++) {
            //ChordProtocol cp = (ChordProtocol) Network.get(i).getProtocol(0);
            ChordProtocol cp = (ChordProtocol) Network.get(i).getProtocol(chordPosition);
            double angle = getAngle(cp);
            PPath node = nodePosition(angle);
            node.setStroke(new PFixedWidthStroke());
            nodeLayer.addChild(node);
            storeInfo(node, cp);
            hashTable.put(Network.get(i).getID(), node);
        }
    }

    private void storeInfo(PNode node, ChordProtocol cp) {
        node.addAttribute("predecessor", cp.predecessor.getID());
        node.addAttribute("successor", cp.successorList[0].getID());
        node.addAttribute("fingers", new ArrayList());
        int SIZE = cp.fingerTable.length;
        for (int i = 0; i < SIZE; i++) {
            ((ArrayList) node.getAttribute("fingers")).add(cp.fingerTable[i].getID());
        }
        node.addAttribute("chordId", cp.chordId.toString());
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

    private PPath drawLine(PNode start, PNode end) {
        PPath line = PPath.createLine(
                (float) start.getFullBoundsReference().getCenter2D().getX(),
                (float) start.getFullBoundsReference().getCenter2D().getY(),
                (float) end.getFullBoundsReference().getCenter2D().getX(),
                (float) end.getFullBoundsReference().getCenter2D().getY());
        line.setStroke(new PFixedWidthStroke());
        return line;
    }
    
    public String tooltipText(PNode aNode){
        String tooltipText;
        String chordId = (String) aNode.getAttribute("chordId");
        tooltipText = "Node ID#: " + chordId + "\n";
        /*String predChordId = (String) ((PNode) getRelationships().get(
                (Long)aNode.getAttribute("predecessor"))
                ).getAttribute("chordId");
        tooltipText = tooltipText.concat("Pred ID#: " + predChordId + "\n");
        String succChordId = (String) ((PNode) getRelationships().get(
                (Long)aNode.getAttribute("successor"))
                ).getAttribute("chordId");
        tooltipText = tooltipText.concat("Succ ID#: " + succChordId + "\n");
        ArrayList fingers = (ArrayList) aNode.getAttribute("fingers");
        for(int i=0;i<fingers.size();i++){
            String fingerId = (String) ((PNode) getRelationships().get(
                (Long)fingers.get(i))
                ).getAttribute("chordId");
            if(i == fingers.size()-1){
                tooltipText = tooltipText.concat("Finger " + i + " ID#: " + fingerId);
            } else {
                tooltipText = tooltipText.concat("Finger " + i + " ID#: " + fingerId + "\n");
            }
        }
        System.out.println(tooltipText);*/
        return tooltipText;
    }

    public class ChordMouseEventHandler extends PBasicInputEventHandler {

        PInputEventFilter filter = new PInputEventFilter();
        ArrayList lines = new ArrayList();
        Boolean selectedSomething = false;
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
                pred = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("predecessor"));
                succ = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("successor"));
                ArrayList fingerID = (ArrayList) e.getPickedNode().getAttribute("fingers");
                System.out.println(fingerID.size());
                fingerNodes = new ArrayList();
                
                lines.add(drawLine(e.getPickedNode(), pred));
                lines.add(drawLine(e.getPickedNode(), succ));
                for (int i = 0; i < fingerID.size(); i++) {
                    fingerNodes.add(getRelationships().get((Long) fingerID.get(i)));
                    ((PNode)fingerNodes.get(i)).setPaint(Color.YELLOW);
                    ((PNode)fingerNodes.get(i)).moveToFront();
                    lines.add(drawLine(e.getPickedNode(), (PNode) fingerNodes.get(i)));
                }
                e.getPickedNode().setPaint(Color.GREEN);
                pred.setPaint(Color.RED);
                succ.setPaint(Color.BLUE);
                succ.moveToFront();
                pred.moveToFront();
                e.getPickedNode().moveToFront();
                edgeLayer.addChildren(lines);
            }
        }

        @Override
        public void mouseClicked(PInputEvent e) {
            super.mouseClicked(e);
            if (e.getButton() == MouseEvent.BUTTON1) {
                selectedSomething = !selectedSomething;
                if (selectedSomething) {
                    something = e.getPickedNode();
                    filter.setAcceptsMouseExited(false);
                    filter.setAcceptsMouseEntered(false);
                    //e.getPickedNode().removeInputEventListener(this);
                } else {
                    if(something.equals(e.getPickedNode())){
                        filter.setAcceptsMouseExited(true);
                        filter.setAcceptsMouseEntered(true);
                    } else {
                        selectedSomething = !selectedSomething;
                    }
                }
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            super.mouseExited(e);
            if (e.getButton() == MouseEvent.NOBUTTON) {
                //PNode pred = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("predecessor"));
                //PNode succ = (PNode) getRelationships().get((Long) e.getPickedNode().getAttribute("successor"));
                e.getPickedNode().setPaint(Color.WHITE);
                pred.setPaint(Color.WHITE);
                succ.setPaint(Color.WHITE);
                e.getPickedNode().moveToBack();
                succ.moveToBack();
                pred.moveToBack();
                edgeLayer.removeChildren(lines);
                int fingersSize = fingerNodes.size();
                for(int i=0;i<fingersSize;i++){
                    ((PNode)fingerNodes.get(i)).setPaint(Color.WHITE);
                    ((PNode)fingerNodes.get(i)).moveToBack();
                }
                int linesSize = lines.size();
                for (int i = 0; i < linesSize; i++) {
                    lines.remove(0);
                }
            }
        }
    }
    
    private class TooltipHandler extends PBasicInputEventHandler{
        PSwing tooltip;
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        JLabel label = new JLabel();
        
        public TooltipHandler(){
            //textArea.setEditable(false);
            //panel.add(textArea, BorderLayout.CENTER);
            panel.add(label, BorderLayout.CENTER);
            tooltip = new PSwing(panel);
            tooltip.setVisible(false);
            tooltip.setPickable(false);
            nodeLayer.addChild(tooltip);
        }
        
        @Override
        public void mouseEntered(PInputEvent e){
            PNode node = e.getPickedNode();
            String text = tooltipText(node);
            //textArea.setText(text);
            label.setText(text);
            tooltip.setVisible(true);
            tooltip.moveToFront();
        }
        
        @Override
        public void mouseMoved(PInputEvent e){
            if(tooltip.getVisible()){
                PNode picked = e.getPickedNode();
                Point2D point = e.getPositionRelativeTo(picked);
                picked.localToParent(point);
                point.setLocation(point.getX()+10, point.getY()+15);
                tooltip.setOffset(point);
            }
        }
        
        @Override
        public void mouseExited(PInputEvent e){
             tooltip.setVisible(false);
        }
    }
}
