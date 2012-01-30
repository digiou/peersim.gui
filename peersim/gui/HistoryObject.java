/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.util.ArrayList;
import peersim.core.Node;


/**
 *
 * @author Dimitris
 */
public class HistoryObject{
    
    private final int NodeSize;
    private ArrayList<HistoryNode> Nodes;
    private final long TimeStamp;
    private final String UpdateReason;
    
    public HistoryObject(Node[] nodes, int nodeSize, long timeStamp, String updateReason){
        this.NodeSize = new Integer(nodeSize);
        this.Nodes = new ArrayList<HistoryNode>();
        for(int i=0;i<this.NodeSize;i++){
            Nodes.add(new HistoryNode(nodes[i]));
        }
        this.TimeStamp = timeStamp;
        this.UpdateReason = updateReason;
        
    }
    
    public int size(){
        return NodeSize;
    }
    
    public ArrayList<HistoryNode> getNodes(){
        return Nodes;
    }
    
    public HistoryNode getNode(int index){
        return Nodes.get(index);
    }
    
    public long getTime(){
        return TimeStamp;
    }
    
    public String getReason(){
        return UpdateReason;
    }
}
