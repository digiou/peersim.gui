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
public class NetworkHistory {
    
    private ArrayList<HistoryObject> History = new ArrayList<HistoryObject>();
    private int pointer = 0;
    
    private NetworkHistory(){}
    
    public synchronized void addToHistory(Node[] someNodes, long timeStamp, String updateCause){
        History.add(new HistoryObject(someNodes, timeStamp, updateCause));
    }
    
    public synchronized HistoryObject getHistory(int index){
        return History.get(index);
    }
    
    public synchronized void resetHistory(){
        pointer = 0;
        History = new ArrayList<HistoryObject>();
    }
    
}
