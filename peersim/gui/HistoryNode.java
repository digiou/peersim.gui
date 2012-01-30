/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import peersim.chord.ChordProtocol;
import peersim.core.Node;

/**
 *
 * @author Dimitris
 */
public final class HistoryNode{
    
    private final int protocolSize;
    private final int index;
    private final long ID;
    private HistoryChordProtocol protocol;
    
    public HistoryNode(Node aNode){
        this.protocolSize = new Integer(aNode.protocolSize());
        this.index = new Integer(aNode.getIndex());
        this.ID = new Long(aNode.getID());
        for(int i = 0; i < this.protocolSize; i++){
            if(aNode.getProtocol(i).getClass() == ChordProtocol.class){
                this.protocol = new HistoryChordProtocol((ChordProtocol)aNode.getProtocol(i), this.ID);
            }
        }
    }
    
    public int protocolSize(){
        return protocolSize;
    }
    
    public final long getID(){
        return ID;
    }
    
    public HistoryChordProtocol getProtocol(){
        return protocol;
    }
    
    public int getIndex(){
        return index;
    }
}
