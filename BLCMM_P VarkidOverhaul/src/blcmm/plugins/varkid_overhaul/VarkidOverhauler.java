package blcmm.plugins.varkid_overhaul;

import blcmm.plugins.BLCMMModelPlugin;
import blcmm.plugins.pseudo_model.PCategory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author LightChaosman
 */
public class VarkidOverhauler extends BLCMMModelPlugin {

    private VarkidPanel panel;

    public VarkidOverhauler() {
        super(true, false, "LightChaosman", "1.0");
    }

    @Override
    public String getName() {
        return "Varkid Overhaul Generator";
    }

    @Override
    public JPanel getGUI() {
        panel = new VarkidPanel();
        JPanel pan = new JPanel();
        pan.setLayout(new GridBagLayout());
        pan.add(panel, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        return pan;
    }

    @Override
    public String[] getRequiredDataClasses() {
        return new String[]{"AttributeInitializationDefinition", "AIPawnBalanceDefinition", "WillowPopulationDefinition", "PopulationFactoryBalancedAIPawn"};
    }

    @Override
    public PCategory getOutputModel() {
        return panel.generate();
    }

    @Override
    public JProgressBar getProgressBar() {
        return null;
    }

}
