package peersim.gui;

//piccolo2d imports
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

//peersim imports
import peersim.core.Network;
import peersim.core.Linkable;

//java utils imports
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author jim
 */
public class Tryout extends PFrame {

    private static final Dimension SCREEN = new Dimension(1024, 768);
    private static final int NODES = Network.size();
    private PCamera camera;
    
    public Tryout(){
        super("Tryout", false, new PSwingCanvas());
    }

    @Override
    public void initialize() {
        setSize(SCREEN);
        final PCanvas canvas = getCanvas();
        camera = canvas.getCamera();

        PLayer nodeLayer = canvas.getLayer();
        PLayer edgeLayer = new PLayer();
        canvas.getRoot().addChild(edgeLayer);
        camera.addLayer(0, edgeLayer);

        Random random = new Random();
        for (int i = 0; i < NODES; i++) {
            float x = random.nextInt(1024);
            float y = random.nextInt(768);
            PPath node = PPath.createEllipse(x, y, 16, 16);
            node.addAttribute("edges", new ArrayList());
            nodeLayer.addChild(node);
        }

        for (int i = 0; i < NODES; i++) {
            int n1 = i;
            Linkable p = (Linkable) Network.get(i).getProtocol(1);
            for (int j = 0; j < p.degree(); j++) {
                int n2 = p.getNeighbor(j).getIndex();

                PNode node1 = nodeLayer.getChild(n1);
                PNode node2 = nodeLayer.getChild(n2);
                PPath edge = new PPath();
                ((ArrayList) node1.getAttribute("edges")).add(edge);
                ((ArrayList) node2.getAttribute("edges")).add(edge);
                edge.addAttribute("nodes", new ArrayList());
                ((ArrayList) edge.getAttribute("nodes")).add(node1);
                ((ArrayList) edge.getAttribute("nodes")).add(node2);
                edgeLayer.addChild(edge);
                updateEdge(edge);
            }
        }

        nodeLayer.addInputEventListener(new PDragEventHandler() {

            {
                PInputEventFilter filter = new PInputEventFilter();
                filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
                setEventFilter(filter);
            }

            public void mouseEntered(PInputEvent e) {
                super.mouseEntered(e);
                if (e.getButton() == MouseEvent.NOBUTTON) {
                    e.getPickedNode().setPaint(Color.RED);
                    ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
                    for(int i = 0;i < edges.size();i++){
                        PPath edge = (PPath) edges.get(i);
                        PNode node1 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(0);
                        PNode node2 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(1);
                        if(e.getPickedNode().equals(node1)){
                            node2.setPaint(Color.BLUE);
                        }else{
                            node1.setPaint(Color.BLUE);
                        }
                    }
                }
            }

            public void mouseExited(PInputEvent e) {
                super.mouseExited(e);
                if (e.getButton() == MouseEvent.NOBUTTON) {
                    e.getPickedNode().setPaint(Color.WHITE);
                    ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
                    for(int i = 0;i < edges.size();i++){
                        PPath edge = (PPath) edges.get(i);
                        PNode node1 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(0);
                        PNode node2 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(1);
                        node1.setPaint(Color.WHITE);
                        node2.setPaint(Color.WHITE);
                    }
                }
            }

            protected void startDrag(PInputEvent e) {
                super.startDrag(e);
                e.setHandled(true);
                e.getPickedNode().moveToFront();
            }

            protected void drag(PInputEvent e) {
                super.drag(e);

                ArrayList edges = (ArrayList) e.getPickedNode().getAttribute("edges");
                for (int i = 0; i < edges.size(); i++) {
                    Tryout.this.updateEdge((PPath) edges.get(i));
                }
            }
        });

    }

    public void updateEdge(PPath edge) {
        PNode node1 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(0);
        PNode node2 = (PNode) ((ArrayList) edge.getAttribute("nodes")).get(1);
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        edge.reset();
        edge.moveTo((float) start.getX(), (float) start.getY());
        edge.lineTo((float) end.getX(), (float) end.getY());
    }
}
