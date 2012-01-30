/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.math.BigInteger;
import peersim.chord.ChordProtocol;

/**
 *
 * @author Dimitris
 */
public final class HistoryChordProtocol {

    public final int succLSize, varSuccList, m;
    public final BigInteger chordId;
    public final Long predecessor;
    public Long[] fingerTable, successorList;

    public HistoryChordProtocol(ChordProtocol prot, long nodeSimID) {
        this.varSuccList = new Integer(prot.varSuccList);
        this.m = new Integer(prot.m);
        this.chordId = new BigInteger(prot.chordId.toString());
        if(prot.predecessor == null){
            this.predecessor = new Long(nodeSimID);
        } else {
            this.predecessor = new Long(prot.predecessor.getID());
        }
        this.fingerTable = new Long[prot.fingerTable.length];
        for (int i = 0; i < prot.fingerTable.length; i++) {
            if(prot.fingerTable[i] == null){
                this.fingerTable[i] = nodeSimID;
            } else {
                this.fingerTable[i] = new Long(prot.fingerTable[i].getID());
            }
        }
        this.succLSize = new Integer(prot.succLSize);
        this.successorList = new Long[prot.succLSize];
        for (int i = 0; i < prot.succLSize; i++) {
            this.successorList[i] = new Long(prot.successorList[i].getID());
        }
    }
}
