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
    
    private static ArrayList<HistoryObject> History = new ArrayList<HistoryObject>();
    
    private NetworkHistory(){}
    
    public static void addToHistory(Node[] someNodes, int datSize, long timeStamp, String updateCause){
        History.add(new HistoryObject(someNodes, datSize, timeStamp, updateCause));
    }
    
    public static int getSize(){
        return History.size();
    }
    
    public static HistoryObject getEntry(int index){
        return History.get(index);
    }
    
    public static void printHistory(){
        int size = History.size();
        HistoryObject toPrint;
        for(int i=0;i<size;i++){
            toPrint = History.get(i);
            System.out.println("Event of: " + toPrint.getReason()
                    +"\nNetwork size is: " + toPrint.size()
                    + " Number of nodes: " + toPrint.getNodes().size());
        }
    }
    
}
