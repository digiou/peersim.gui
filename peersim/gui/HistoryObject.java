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
    
    private final Node[] Nodes;
    private final long TimeStamp;
    private final String UpdateReason;
    
    public HistoryObject(Node[] nodes, long timeStamp, String updateReason){
        this.Nodes = nodes;
        this.TimeStamp = timeStamp;
        this.UpdateReason = updateReason;
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
