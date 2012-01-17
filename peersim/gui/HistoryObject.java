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
        this.Nodes = nodes;
        this.TimeStamp = timeStamp;
        this.UpdateReason = updateReason;
        this.NodeSize = nodeSize;
    }
    
    public synchronized int getSize(){
        return NodeSize;
    }
    
    public synchronized Node[] getNodes(){
        return Nodes;
    }
    
    public synchronized long getTime(){
        return TimeStamp;
    }
    
    public synchronized String getReason(){
        return UpdateReason;
    }
}
