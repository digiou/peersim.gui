
package peersim.gui;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.lang.Math;

/**
 *
 * @author jim
 */
public class Ring extends PFrame{
    
    private static final Dimension SCREEN = new Dimension(1024, 768);
    private PCamera camera;
    
    public static void main(String[] args){
        new Ring();
    }
    
    public Ring(){
        super("Ring", false, new PSwingCanvas());
    }
    
    @Override
    public void initialize(){
        setSize(SCREEN);
        Point2D center = new Point2D.Double((float) SCREEN.width / 2, (float) SCREEN.height);
        ArrayList<Point2D> circlePoints = circlePoints(center, (float) SCREEN.height / 2);
        for(int i = 0;i<360;i++){
            Point2D point = circlePoints.get(i);
            PPath node = PPath.createEllipse((float)point.getX(), (float)point.getY(), 16, 16);
            getCanvas().getLayer().addChild(node);
        }
    }
    
    private ArrayList<Point2D> circlePoints(Point2D center, double r){
        double cx = center.getX();
        double cy = center.getY();
        double x, y, a;
        
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        for(int i = 0; i < 360;i++){
            //x = cx + r * cos(a)
            //y = cy + r * sin(a)
            a = Math.toRadians(i);
            x = cx + (r*Math.cos(a));
            y = cy + (r*Math.sin(a));
            points.add(new Point2D.Double(x, y));
        }
        return points;
    }
}
