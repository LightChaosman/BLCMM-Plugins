package blcmm.plugins.timesxspawns;

import blcmm.plugins.BLCMMModelPlugin;
import blcmm.plugins.pseudo_model.PCategory;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * This is the implementation of the actual plugin. It just returns a name, and
 * a few classes whose data needs to be present for the plugin to work properly.
 * All of the actual work is done in the TimesXSpawnsPanel class. This is the
 * proper GUI for this plugin. A simpler version of the GUI is presented in
 * TimesXSpawnsPanelSimple. Here we don't use multithreading, or hand-written
 * GUI generation. The simpler version is just a wrapper for the actual mod
 * generator, which is contained in DenDumpProcessor.java
 *
 * @author LightChaosman
 */
public class TimesXSpawns extends BLCMMModelPlugin {

    private JProgressBar bar = new JProgressBar();
    private TimesXSpawnsPanel panel;

    public TimesXSpawns() {
        super(true, true, "LightChaosman", "1.2.0");//true,true indicates this plugin works for both BL2 and TPS
        bar.setPreferredSize(new Dimension(300, 25));
    }

    @Override
    public String getName() {
        return "Times X spawns mod generator";
    }

    @Override
    public JPanel getGUI() {
        this.panel = new TimesXSpawnsPanel(this);
        return panel;
    }

    @Override
    public String[] getRequiredDataClasses() {
        return new String[]{//These are the classes of the borderlands objects that are used by this plugin.
            //By providing them here, BLCMM can disable access to the plugin if data is missing, so the plugin won't crash
            "PopulationOpportunityDen",
            "PopulationFactoryBalancedAIPawn",
            "AIPawnBalanceDefinition",
            "WillowAIPawn",
            "AIClassDefinition"};
    }

    @Override
    public PCategory getOutputModel() {
        return panel.generate();
    }

    @Override
    public JProgressBar getProgressBar() {
        return bar;
    }

}
