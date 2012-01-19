/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import peersim.core.Node;


/**
 *
 * @author Dimitris
 */
public class HistoryObject{
    
    private final int NodeSize;
    private final Node[] Nodes;
    private final long TimeStamp;
    private final String UpdateReason;
    
    public HistoryObject(Node[] nodes, int nodeSize, long timeStamp, String updateReason){
        this.Nodes = nodes.clone();
        this.TimeStamp = timeStamp;
        this.UpdateReason = updateReason;
        this.NodeSize = nodeSize;
    }
    
    public int size(){
        return NodeSize;
    }
    
    public Node[] getNodes(){
        return Nodes;
    }
    
    public Node getNode(int index){
        return Nodes[index];
    }
    
    public long getTime(){
        return TimeStamp;
    }
    
    public String getReason(){
        return UpdateReason;
    }
}
