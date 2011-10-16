
package peersim.gui;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

/**
 *  Returns a routing table for all the nodes of our
 *  network. Here we go!
 * 
 * @author jim
 */
public class NeighborsExtractor implements Control{


// ===================== fields =======================================
// ====================================================================

	
/**
 * The protocol to operate on, from the configuration.
 * @config
 */
private static final String PAR_PROT = "protocol";


/** The name of this observer in the configuration */
protected final String name;
/** The ID of the protocol we operate on. */
protected final int pid;


// ===================== initialization ================================
// =====================================================================
/**
 * 
 * @param name = The name of this extractor in the
 * configuration file.
 */
protected NeighborsExtractor(String name){
	this.name = name;
	pid = Configuration.getPid(name+"."+PAR_PROT);
}

// ===================== Control implementations ======================
	@Override
	public boolean execute() {
		for(int i = 0; i<Network.size(); ++i){
			Node n = (Node) Network.get(i);
			Linkable p = (Linkable) n.getProtocol(pid);
			for(int j = 0; j<p.degree(); ++j){
				System.out.println("Node: "+ i + " has neighbor: " + p.getNeighbor(j));
			}
			System.out.println();
		}
		return false;
	}
	
}