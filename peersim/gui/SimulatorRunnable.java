/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.io.*;

import peersim.cdsim.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

/**
 *
 * @author Dimitris
 */
public class SimulatorRunnable implements Runnable {
    
    private final String[] args;
    
    public SimulatorRunnable(String[] inheritedArgs){
        super();
        this.args = inheritedArgs;
    }

    // ========================== static constants ==========================
// ======================================================================
    /**
     * {@link CDSimulator}
     */
    public static final int CDSIM = 0;
    public static final int EDSIM = 1;
    public static final int UNKNOWN = -1;
    /**
     * the class names of simulators used
     */
    protected static final String[] simName = {
        "peersim.cdsim.CDSimulator",
        "peersim.edsim.EDSimulator",};
    /**
     * Parameter representing the number of times the experiment is run.
     */
    public static final String PAR_EXPS = "simulation.experiments";
    /**
     * If present, this parameter activates the redirection of the standard
     * output to a given PrintStream.
     */
    public static final String PAR_REDIRECT = "simulation.stdout";
// ==================== static fields ===================================
// ======================================================================
    /**
     *      */
    private static int simID = UNKNOWN;

//========================== methods ===================================
//======================================================================
    /**
     * Returns the numeric id of the simulator to invoke.
     */
    public static int getSimID() {

        if (simID == UNKNOWN) {
            if (CDSimulator.isConfigurationCycleDriven()) {
                simID = CDSIM;
            } else if (EDSimulator.isConfigurationEventDriven()) {
                simID = EDSIM;
            }
        }
        return simID;
    }

    @Override
    public void run() {

        long time = System.currentTimeMillis();

        System.err.println("Simulator: loading configuration");
        Configuration.setConfig(new ParsedProperties(args));

        PrintStream newout =
                (PrintStream) Configuration.getInstance(PAR_REDIRECT, System.out);
        if (newout != System.out) {
            System.setOut(newout);
        }

        int exps = Configuration.getInt(PAR_EXPS, 1);

        final int SIMID = getSimID();
        if (SIMID == UNKNOWN) {
            System.err.println(
                    "Simulator: unable to determine simulation engine type");
            return;
        }

        try {

            for (int k = 0; k < exps; ++k) {
                if (k > 0) {
                    long seed = CommonState.r.nextLong();
                    CommonState.initializeRandom(seed);
                }
                System.err.print("Simulator: starting experiment " + k);
                System.err.println(" invoking " + simName[SIMID]);
                System.err.println("Random seed: "
                        + CommonState.r.getLastSeed());
                System.out.println("\n\n");

                // XXX could be done through reflection, but
                // this is easier to read.
                switch (SIMID) {
                    case CDSIM:
                        CDSimulator.nextExperiment();
                        break;
                    case EDSIM:
                        EDSimulator.nextExperiment();
                        break;
                }
            }

        } catch (MissingParameterException e) {
            System.err.println(e + "");
            System.exit(1);
        } catch (IllegalParameterException e) {
            System.err.println(e + "");
            System.exit(1);
        }

        // undocumented testing capabilities
        if (Configuration.contains("__t")) {
            System.out.println(System.currentTimeMillis() - time);
        }
        if (Configuration.contains("__x")) {
            Network.test();
        }
    }
}
