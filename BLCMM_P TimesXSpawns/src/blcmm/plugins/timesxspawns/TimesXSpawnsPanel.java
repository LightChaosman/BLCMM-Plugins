package blcmm.plugins.timesxspawns;

import blcmm.data.lib.DataManager;
import blcmm.data.lib.sdk.generated.WillowGame.PopulationOpportunityDen;
import blcmm.plugins.pseudo_model.PCategory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * The main code of the first plugin for BLCMM is in here. This shows several
 * ways to interact with the API provided by the BLCMM utilities and the data
 * interaction library.
 *
 * I deliberately chose to code part of the UI manually, to show two ways of
 * doing so. One part is made by the editor in Netbeans, whilst the other is
 * made by handwritten code using a GridBagLayout.
 *
 * I made the actual mod-generation run in a seperate thread, to be able to show
 * a progress bar. This is optional but recommended for lengthy mod-generations,
 * so the UI does not freeze, and the user has some idea of how much longer he
 * needs to wait.
 *
 *
 * @author LightChaosman
 */
public final class TimesXSpawnsPanel extends javax.swing.JPanel {

    private boolean checkBox1OldState;
    private final TimesXSpawns plugin;
    private JSpinner postAddSpinner;
    private JSpinner preAddSpinner;
    private JSpinner scaleSpinner;

    /**
     * Creates new form TimesXSpawnsPanel
     *
     * @param plugin The plugin that generated this GUI, use it to get the
     * progress bar
     */
    public TimesXSpawnsPanel(TimesXSpawns plugin) {
        initComponents();
        this.plugin = plugin;
        buildPanel();
        updateUI();
    }

    private void buildPanel() { //This is a (slightly messy) example of how to code a GUI by hand
        //To learn how to do this, read up on how a GridBagLayout works

        //First we define the componenents we're gonna add
        JLabel infoLabel = new JLabel("Number of spawns will be:");
        JLabel formulaLabel = new JLabel("(Original+PreAdd)*Scale+PostAdd");
        JLabel preaddLabel = new JLabel("PreAdd");
        JLabel scaleLabel = new JLabel("Scale");
        JLabel postaddLabel = new JLabel("PostAdd");

        preAddSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
        scaleSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));
        postAddSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
        JCheckBox advancedCheckbox = new JCheckBox("I understand mathematics");
        ActionListener l = (ActionEvent e) -> {
            boolean selected = advancedCheckbox.isSelected();
            preaddLabel.setVisible(selected);
            preAddSpinner.setVisible(selected);
            postaddLabel.setVisible(selected);
            postAddSpinner.setVisible(selected);
            formulaLabel.setText(selected ? "(Original+PreAdd)*Scale+PostAdd" : "Original times x");
            scaleLabel.setText(selected ? "Scale" : "x");
            updateUI();
        };
        advancedCheckbox.addActionListener(l);

        //Then we build the GUI
        jPanel2.setLayout(new GridBagLayout());
        jPanel2.add(infoLabel, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(formulaLabel, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(preaddLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(preAddSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(scaleLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(scaleSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(postaddLabel, new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(postAddSpinner, new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        jPanel2.add(advancedCheckbox, new GridBagConstraints(0, 6, 2, 1, 1, 1, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        jPanel2.add(Box.createVerticalGlue(), new GridBagConstraints(0, 5, 2, 1, 1, 1000000000, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        updateUI();//We let the UI scale itself to accomodate for the advanced settings
        l.actionPerformed(null);//Now we set it to simple mode
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        NPCCheckBox = new javax.swing.JCheckBox();
        BadassCheckBox = new javax.swing.JCheckBox();
        BossCheckBox = new javax.swing.JCheckBox();
        FlyingCheckBox = new javax.swing.JCheckBox();
        TurretCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Other options"));

        NPCCheckBox.setSelected(true);
        NPCCheckBox.setText("Don't change NPCs");
        NPCCheckBox.setToolTipText("NPCs are disabled by default");
        NPCCheckBox.setEnabled(false);

        BadassCheckBox.setText("Don't change badass spawns");
        BadassCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                BadassCheckBoxStateChanged(evt);
            }
        });

        BossCheckBox.setText("Don't change boss spawns");

        FlyingCheckBox.setText("Don't change flying spawns");

        TurretCheckBox.setSelected(true);
        TurretCheckBox.setText("Don't change turrets");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NPCCheckBox)
                    .addComponent(BadassCheckBox)
                    .addComponent(BossCheckBox)
                    .addComponent(FlyingCheckBox)
                    .addComponent(TurretCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NPCCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BadassCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BossCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FlyingCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TurretCheckBox)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Spawn options"));
        jPanel2.setAutoscrolls(true);
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 200));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 202, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BadassCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_BadassCheckBoxStateChanged
        //Completely unnescesary, but this makes the bosses checkmark automatically ticked if all badasses are ignored
        if (BadassCheckBox.isSelected()) {
            if (BossCheckBox.isEnabled()) {
                checkBox1OldState = BossCheckBox.isSelected();
            }
            BossCheckBox.setSelected(true);
            BossCheckBox.setEnabled(false);
            BossCheckBox.setToolTipText("All bosses are badasses");
        } else {
            if (!BossCheckBox.isEnabled()) {
                BossCheckBox.setSelected(checkBox1OldState);
            }
            BossCheckBox.setEnabled(true);
            BossCheckBox.setToolTipText("");
        }
    }//GEN-LAST:event_BadassCheckBoxStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BadassCheckBox;
    private javax.swing.JCheckBox BossCheckBox;
    private javax.swing.JCheckBox FlyingCheckBox;
    private javax.swing.JCheckBox NPCCheckBox;
    private javax.swing.JCheckBox TurretCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    PCategory generate() {

        final boolean skipBosses = BossCheckBox.isSelected();
        final boolean skipBadasses = BadassCheckBox.isSelected();
        final boolean skipFlyers = FlyingCheckBox.isSelected();
        final boolean skipNPC = NPCCheckBox.isSelected();
        final boolean skipTurrets = TurretCheckBox.isSelected();
        final int preadd = (int) preAddSpinner.getValue();
        final int scale = (int) scaleSpinner.getValue();
        final int postadd = (int) postAddSpinner.getValue();
        if (preadd == 0 && scale == 1 && postadd == 0) {
            return null;
        }
        PCategory root = new PCategory("Times X spawns");
        CodeGenerator dumpProcessor = new CodeGenerator(skipBosses, skipBadasses, skipFlyers, skipNPC, skipTurrets, preadd, scale, postadd, root, plugin);
        CodeGenerator_SDK dumpProcessor2 = new CodeGenerator_SDK(skipBosses, skipBadasses, skipFlyers, skipNPC, skipTurrets, preadd, scale, postadd, root, plugin);
        final int denCount = DataManager.getDictionary().getClassStreamSize("PopulationOpportunityDen", true);
        plugin.getProgressBar().setMaximum(denCount);
        // DataManager.streamAllDumpsOfClassAndSubclasses("PopulationOpportunityDen", dumpProcessor);
        DataManager.streamAllObjectsOfClassAndSubclasses(PopulationOpportunityDen.class).forEach(dumpProcessor2);
        return root;
    }

}
