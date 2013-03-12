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
    
    private static ArrayList<HistoryObject> EventHistory = new ArrayList<HistoryObject>();
    private static ArrayList<Node[]> DiffHistory = new ArrayList();
    
    private NetworkHistory(){}
    
    public static void addToHistory(Node[] someNodes, int datSize, long timeStamp, String updateCause){
        if(updateCause.equals("diff")){
            DiffHistory.add(someNodes);
        } else {
            EventHistory.add(new HistoryObject(someNodes, datSize, timeStamp, updateCause));
        }
    }
    
    public static int getSize(){
        return EventHistory.size();
    }
    
    public static HistoryObject getEntry(int index){
        return EventHistory.get(index);
    }
    
    public static Node[] getDiff(int index){
        return DiffHistory.get(index);
    }
    
    public static void printHistory(){
        int size = EventHistory.size();
        HistoryObject toPrint;
        for(int i=0;i<size;i++){
            toPrint = EventHistory.get(i);
            System.out.println("Event of: " + toPrint.getReason()
                    +"\nNetwork size is: " + toPrint.size()
                    + " Number of nodes: " + toPrint.getNodes().size());
        }
    }
    
}
