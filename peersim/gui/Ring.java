package peersim.gui;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import peersim.core.Linkable;
import peersim.core.Network;

/**
 *
 * @author jim
 */
public class Ring extends PFrame {

    private static final Dimension SCREEN = new Dimension(1024, 768);
    private static final int NODES = Network.size();
    private PCamera camera;

    public static void main(String[] args) {
        new Ring();
    }

    public Ring() {
        super("Ring", false, new PSwingCanvas());
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

        Point2D center = new Point2D.Double((float) SCREEN.width / 2, (float) SCREEN.height);
        ArrayList<Point2D> circlePoints = circlePoints(center, (float) SCREEN.height / 2);
        for (int i = 0; i < NODES; i++) {
            Point2D point = circlePoints.get(i);
            PPath node = PPath.createEllipse((float) point.getX(), (float) point.getY(), 16, 16);
            node.addAttribute("edges", new ArrayList());
            node.addAttribute("prevPeers", new ArrayList());
            node.addAttribute("nextPeers", new ArrayList());
            nodeLayer.addChild(node);
        }

        for (int i = 0; i < NODES; i++) {
            int n1 = i;
            Linkable p = (Linkable) Network.get(i).getProtocol(1);
            for (int j = 0; j < p.degree(); j++) {
                int n2 = p.getNeighbor(j).getIndex();

                PNode node1 = nodeLayer.getChild(n1);
                PNode node2 = nodeLayer.getChild(n2);
                Point2D start = node1.getFullBoundsReference().getCenter2D();
                Point2D end = node2.getFullBoundsReference().getCenter2D();
                PPath edge = new PPath(createArrow(new Point2D.Double(start.getX(), start.getY()),
                        new Point2D.Double(end.getX(), end.getY())));


                ((ArrayList) node1.getAttribute("edges")).add(edge);
                ((ArrayList) node2.getAttribute("edges")).add(edge);
                edge.addAttribute("nodes", new ArrayList());
                ((ArrayList) edge.getAttribute("nodes")).add(node1);
                ((ArrayList) edge.getAttribute("nodes")).add(node2);
                edgeLayer.addChild(edge);
            }
        }
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

    private ArrayList<Point2D> circlePoints(Point2D center, double r) {
        double cx = center.getX();
        double cy = center.getY();
        double x, y, a;

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        for (int i = 0; i < 360; i++) {
            a = Math.toRadians(i);
            x = cx + (r * Math.cos(a));
            y = cy + (r * Math.sin(a));
            points.add(new Point2D.Double(x, y));
        }
        return points;
    }
}
