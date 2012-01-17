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
    
    public void resetHistory(){
        History = new ArrayList<HistoryObject>();
    }
    
    public static void printHistory(){
        int size = History.size();
        HistoryObject toPrint;
        for(int i=0;i<size;i++){
            toPrint = History.get(i);
            System.out.println("An event of: " + toPrint.getReason() +
                    " happened at: " + toPrint.getTime()
                    +"\nNetwork size is: " + toPrint.getSize());
        }
    }
    
}
