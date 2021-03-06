package blcmm.plugins.varkid_overhaul;

import blcmm.data.lib.BorderlandsArray;
import blcmm.data.lib.BorderlandsObject;
import blcmm.data.lib.BorderlandsStruct;
import blcmm.data.lib.DataManager;
import blcmm.model.HotfixType;
import blcmm.plugins.pseudo_model.PCategory;
import blcmm.plugins.pseudo_model.PComment;
import blcmm.plugins.pseudo_model.PHotfix;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 *
 * @author LightChaosman
 */
@SuppressWarnings("serial")
public class VarkidPanel extends javax.swing.JPanel {

    private static final String TOOLTIP1 = "Simply buffs the global varkid evolution chance. Each stage of evolution is buffed by this amount, so it applies 5 times on the entire chain from larval to vermi.";
    private static final String TOOLTIP2 = "The chance for a varkid to skip over evolutions. Can skip multiple evolutions.";
    private static final String TOOLTIP3 = "The odds of low-tiered varkids to drop gear normally only dropped by evolved varkids";

    private static enum Player {
        PLAYER1(1), PLAYER2(2), PLAYER3(3), PLAYER4(4);
        final int players;

        private Player(int players) {
            this.players = players;
        }

        @Override
        public String toString() {
            return players + " Player" + (players > 1 ? "s" : "");
        }
    }

    private static enum Playtrough {
        NVHM(0, "NVHM"), UVHM(1, "TVHM/UVHM");
        private final int playtrough;
        private final String name;

        private Playtrough(int playtrough, String name) {
            this.playtrough = playtrough;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getShortName() {
            switch (this) {
                case NVHM:
                    return "nvhm";
                case UVHM:
                    return "uvhm";
                default:
                    return null;
            }
        }

    }

    private final static String DESCRIPTION
            = "Globally increases all varkid evolution by %s%%.\n"
            + "Varkids have a %s%% chance to skip evolutions.\n"
            + "Varkids have a %s%% chance to drop lootpools from higher evolutions, with some scaling.\n%s%s"
            + "The varkids in the crawmerax DLC do not skip evolutions or loot.\n"
            + "\n"
            + "These changes apply repeatedly. So varkids can skip one, two, three,... etc evolutions at once, and drop lootpools from one, two, three,... etc evolutions down the line.\n"
            + "Odds for skipping multiple evolutions naturally decreases with how many are skipped.\n"
            + "\n"
            + "The purpose of this mod is to speed up varkid farming, and make it more enjoyable, by \n"
            + "A) making varkids reach ulti or vermi faster\n"
            + "B) by giving lower evolutions a small chance to drop the loot of ultis and vermi\n"
            + "\n"
            + "Bonus: Since each varkid has 3 chances to go into a tubby, tubby varkid farming is also faster with this mod.";

    /**
     * Creates new form VarkidPanel
     *
     */
    public VarkidPanel() {
        initComponents();
        jComboBox1.setSelectedItem(Player.PLAYER2);
        jComboBox2.setSelectedItem(Playtrough.UVHM);
        jSlider1StateChanged(null);
        jSlider2StateChanged(null);
        jSlider3StateChanged(null);
        jSlider3.setToolTipText(TOOLTIP1);
        jLabel6.setToolTipText(TOOLTIP1);
        jLabel7.setToolTipText(TOOLTIP1);
        jSlider1.setToolTipText(TOOLTIP2);
        jLabel1.setToolTipText(TOOLTIP2);
        jLabel2.setToolTipText(TOOLTIP2);
        jSlider2.setToolTipText(TOOLTIP3);
        jLabel3.setToolTipText(TOOLTIP3);
        jLabel4.setToolTipText(TOOLTIP3);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>(Player.values());
        jComboBox2 = new javax.swing.JComboBox<>(Playtrough.values());
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jSlider2 = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        jLabel5.setText("Use odds of:");

        jComboBox1.setPreferredSize(new java.awt.Dimension(110, 25));

        jComboBox2.setPreferredSize(new java.awt.Dimension(110, 25));

        jCheckBox2.setText("Spawn evolved varkids");
        jCheckBox2.setToolTipText("Checking this will spawn evolved varkids based on the choices below");

        jCheckBox1.setText("Carry loot to next evolutions");
        jCheckBox1.setToolTipText("Makes it so vermi also drops the Quasar. Does not crowd his lootpool.");

        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jLabel3.setText("Chance to skip lootpools:");

        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel1.setText("Chance to skip evolutions: ");

        jSlider3.setMaximum(1000);
        jSlider3.setValue(250);
        jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });

        jLabel6.setText("Buff varkid evolution:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("25%");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("25%");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("25.0%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider3, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel5))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        jLabel4.setText(jSlider2.getValue() + "%");
    }//GEN-LAST:event_jSlider2StateChanged

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        jLabel2.setText(jSlider1.getValue() + "%");
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
        jLabel7.setText(String.format("%.1f", jSlider3.getValue() / 10d) + "%");
    }//GEN-LAST:event_jSlider3StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox<Player> jComboBox1;
    private javax.swing.JComboBox<Playtrough> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    // End of variables declaration//GEN-END:variables

    PCategory generate() {
        DataManager.getDictionary().getElementsWithPrefix("akhdkah");//Force the dictionary to proceed with loading

        double skipchance = jSlider1.getValue() / 100d;
        double lootchance = jSlider2.getValue() / 100d;
        final double[] harderThanPreviousArray = new double[]{1, 2, 1.5, 2.5, 5};
        final int phases = 5;
        final String oddsPrefix = "GD_Balance.WeightingPlayerCount.BugmorphCocoon_PerPlayers_Phase";
        final int playtrough = ((Playtrough) jComboBox2.getSelectedItem()).playtrough;
        final int players = ((Player) jComboBox1.getSelectedItem()).players;
        final int oddindex = playtrough * 4 + players - 1;
        final boolean spawnEvolved = jCheckBox2.isSelected();
        final boolean carryoverLoot = jCheckBox1.isSelected();
        final String[] morphs = new String[]{//
            "GD_Population_BugMorph.Population.PopDef_BugMorphMix_Regular",
            "GD_Population_BugMorph.Population.PopDef_BugMorphAdult",
            "GD_Population_BugMorph.Population.PopDef_BugMorphBadass",
            "GD_Population_BugMorph.Population.PopDef_BugMorphSuperBadass",
            "GD_Population_BugMorph.Population.PopDef_BugMorphUltimateBadass",
            "GD_Population_BugMorph.Population.PopDef_BugMorphRaid"};

        double[] evolutionodds = new double[phases];
        String[] phaseDumps = new String[phases + 1];
        for (int phase = 1; phase <= phases; phase++) {
            phaseDumps[phase] = DataManager.getDump("AttributeInitializationDefinition'" + oddsPrefix + phase + "'").dump;
            BorderlandsObject attinit = BorderlandsObject.parseObject(phaseDumps[phase]);
            evolutionodds[phase - 1] = attinit.getFloatField("ConditionalInitialization.ConditionalExpressionList[" + oddindex + "].BaseValueIfTrue.BaseValueConstant");
        }
        BorderlandsObject[] morphObjects = new BorderlandsObject[phases + 1];
        HashMap<BorderlandsObject, HashMap<BorderlandsObject, BorderlandsArray>> populationToAIPawns = new HashMap<>();
        for (int i = 0; i < phases + 1; i++) {
            morphObjects[i] = BorderlandsObject.parseObject(DataManager.getDump(morphs[i]));
            HashMap<BorderlandsObject, BorderlandsArray> set = new HashMap<>();
            BorderlandsArray<BorderlandsStruct> AAL = morphObjects[i].getArrayField("ActorArchetypeList");
            populationToAIPawns.put(morphObjects[i], set);
            for (int j = 0; j < AAL.size(); j++) {
                BorderlandsObject factory = BorderlandsObject.parseObject(DataManager.getDump(AAL.get(j).getString("SpawnFactory")));
                BorderlandsObject AIPawn = BorderlandsObject.parseObject(DataManager.getDump(factory.getStringField("PawnBalanceDefinition")));
                BorderlandsArray DIPL = AIPawn.getArrayField("DefaultItemPoolList");
                if (DIPL == null) {
                    DIPL = new BorderlandsArray();
                } else {
                    DIPL = BorderlandsArray.parseArray(DIPL.toString());
                }
                set.put(AIPawn, DIPL);
            }
        }
        PCategory root = new PCategory("varkidOverhaul");
        PCategory currentCat = new PCategory("Code");
        root.addChild(currentCat);
        final String hotfixName = "varkidOverhaul";
        final HotfixType hotfixType = HotfixType.LEVEL;
        final String hotfixParam = "None";

        if (jSlider3.getValue() > 0) {
            double buff = jSlider3.getValue() / 1000d;
            double[] excess = new double[]{1, 1, 1, 1, 1, 1, 1, 1};
            for (int phase = 1; phase <= phases; phase++) {
                BorderlandsObject attinit = BorderlandsObject.parseObject(phaseDumps[phase]);
                for (int i = 0; i < 8; i++) {
                    float fieldFloat = attinit.getFloatField("ConditionalInitialization.ConditionalExpressionList[" + i + "].BaseValueIfTrue.BaseValueConstant");
                    double newodd;
                    double unrounded = fieldFloat * (1 + buff) * excess[i];
                    if (unrounded > 1) {
                        newodd = 1;
                        excess[i] = (unrounded - 1) / 2 + 1;
                    } else {
                        newodd = unrounded;
                        excess[i] = 1;
                    }
                    String command = "set " + oddsPrefix + phase + " ConditionalInitialization.ConditionalExpressionList[" + i + "].BaseValueIfTrue.BaseValueConstant " + String.format(Locale.US, "%.6f", newodd);
                    currentCat.addChild(new PHotfix(command, hotfixType, hotfixParam, hotfixName));
                    //result.addLine(command);
                    //System.out.println(command);
                    if (i == oddindex) {
                        evolutionodds[phase - 1] = newodd;
                    }
                }
            }
        }

        for (int i = 0; i < phases; i++) {//-1 since we can't skip vermi
            BorderlandsObject popdef = morphObjects[i];
            BorderlandsArray<BorderlandsStruct> ActorArchetypeList = popdef.getArrayField("ActorArchetypeList");
            HashMap<BorderlandsObject, BorderlandsArray> AIPawnBalances = populationToAIPawns.get(popdef);
            int skip = 1;
            double oddsToSkip = skipchance * evolutionodds[i + skip - 1];
            double oddsToSkipLoot = lootchance * evolutionodds[i + skip - 1] / harderThanPreviousArray[i + skip - 1];
            int reducedIdxes = 0;
            while (oddsToSkip > 0 && i + skip < phases + 1) {
                BorderlandsObject nextevo = morphObjects[i + skip];
                //System.out.println(String.format("current popdef: %s, adding %s", popdef.getName(), nextevo.getName()));
                HashMap<BorderlandsObject, BorderlandsArray> nextEvoBalances = populationToAIPawns.get(nextevo);
                BorderlandsArray<BorderlandsStruct> nextEvoActorArchetypeList = nextevo.getArrayField("ActorArchetypeList");
                if (i > 0 || spawnEvolved) {
                    for (int j = reducedIdxes; j < ActorArchetypeList.size(); j++) {
                        double odd = (ActorArchetypeList.get(j).getFloat("Probability.BaseValueScaleConstant") * (1 - oddsToSkip));
                        ActorArchetypeList.get(j).getStruct("Probability").set("BaseValueScaleConstant", odd);
                    }
                }
                reducedIdxes = ActorArchetypeList.size();
                for (int j = 0; j < nextEvoActorArchetypeList.size(); j++) {
                    if (i > 0 || spawnEvolved) {
                        double odd = (oddsToSkip / evolutionodds[i + skip - 1]);
                        ActorArchetypeList.add(BorderlandsStruct.parseStruct(nextEvoActorArchetypeList.get(j).toString()));//copy, not reference
                        double bvc = ActorArchetypeList.get(ActorArchetypeList.size() - 1).getFloat("Probability.BaseValueConstant");
                        ActorArchetypeList.get(ActorArchetypeList.size() - 1).getStruct("Probability").set("BaseValueConstant", "0.0");
                        ActorArchetypeList.get(ActorArchetypeList.size() - 1).getStruct("Probability").set("BaseValueScaleConstant", odd * bvc);
                        ActorArchetypeList.get(ActorArchetypeList.size() - 1).getStruct("Probability").set("InitializationDefinition", "AttributeInitializationDefinition'" + oddsPrefix + (i + skip) + "'");
                    }
                    double odd = (oddsToSkipLoot / evolutionodds[i + skip - 1]);
                    for (BorderlandsObject currentAIP : AIPawnBalances.keySet()) {
                        BorderlandsArray<BorderlandsStruct> DIPL = AIPawnBalances.get(currentAIP);
                        for (BorderlandsObject evolvedAIP : nextEvoBalances.keySet()) {
                            BorderlandsArray newDIPL = nextEvoBalances.get(evolvedAIP);
                            for (int k = 0; k < newDIPL.size(); k++) {
                                DIPL.add(BorderlandsStruct.parseStruct(newDIPL.get(k).toString()));//copy, not reference
                                double extrafact = DIPL.get(DIPL.size() - 1).getString("PoolProbability.BaseValueAttribute").equals("AttributeDefinition'GD_Itempools.DropWeights.DropODDS_BossUniques'") ? 0.1 : 1;
                                DIPL.get(DIPL.size() - 1).getStruct("PoolProbability").set("BaseValueConstant", "0.0");
                                DIPL.get(DIPL.size() - 1).getStruct("PoolProbability").set("BaseValueAttribute", "None");
                                DIPL.get(DIPL.size() - 1).getStruct("PoolProbability").set("BaseValueScaleConstant", odd * extrafact);
                                DIPL.get(DIPL.size() - 1).getStruct("PoolProbability").set("InitializationDefinition", "AttributeInitializationDefinition'" + oddsPrefix + (i + skip) + "'");
                            }
                        }
                    }
                }

                skip++;
                if (i + skip < phases + 1) {
                    oddsToSkip = oddsToSkip * skipchance * evolutionodds[i + skip - 1];
                    oddsToSkipLoot = oddsToSkipLoot * lootchance * evolutionodds[i + skip - 1] / harderThanPreviousArray[i + skip - 1];
                }
            }

            String command = "set " + popdef.getName() + " ActorArchetypeList " + ActorArchetypeList;
            //result.addLine(command);
            currentCat.addChild(new PHotfix(command, hotfixType, hotfixParam, hotfixName));

        }
        HashSet<String> names = new HashSet<>();
        for (int i = 0; i <= phases; i++) {
            BorderlandsObject popdef = morphObjects[i];
            HashMap<BorderlandsObject, BorderlandsArray> AIPawnBalances = populationToAIPawns.get(popdef);

            addCarryOverLoot(carryoverLoot, i, morphObjects, populationToAIPawns, AIPawnBalances);

            String command;
            for (BorderlandsObject currentAIP : AIPawnBalances.keySet()) {
                BorderlandsArray DIPL = AIPawnBalances.get(currentAIP);
                if (DIPL.size() != 0 && !names.contains(currentAIP.getName())) {
                    command = "set " + currentAIP.getName() + " DefaultItemPoolList " + DIPL;
                    //result.addLine(command);
                    currentCat.addChild(new PHotfix(command, hotfixType, hotfixParam, hotfixName));
                    names.add(currentAIP.getName());
                }
            }
        }
        root.addChild(new PComment(String.format(DESCRIPTION,
                jSlider3.getValue() / 10,//global buff
                jSlider1.getValue(),//skip chance
                jSlider2.getValue(),//loot skip chance
                carryoverLoot ? "Vermi also drops the quasar.\n" : "",
                spawnEvolved ? "Varkids also skip the larval stage, spawning evolved.\n" : "")), 0);
        String settings = String.format("%s-%s-%s-%s-%s-%s", jSlider3.getValue() / 10, jSlider1.getValue(), jSlider2.getValue(), jComboBox1.getSelectedItem(), ((Playtrough) jComboBox2.getSelectedItem()).getShortName(), jCheckBox2.isSelected());
        return root;
    }

    private void addCarryOverLoot(final boolean carryoverLoot, int i, BorderlandsObject[] morphObjects, HashMap<BorderlandsObject, HashMap<BorderlandsObject, BorderlandsArray>> populationToAIPawns, HashMap<BorderlandsObject, BorderlandsArray> AIPawnBalances) {
        if (carryoverLoot) {
            double fac = 2;
            for (int j = i - 1; j >= 0; j--) {
                BorderlandsObject prevevo = morphObjects[j];
                HashMap<BorderlandsObject, BorderlandsArray> prevEvoBalances = populationToAIPawns.get(prevevo);
                for (BorderlandsObject currentAIP : AIPawnBalances.keySet()) {
                    BorderlandsArray<BorderlandsStruct> DIPL = AIPawnBalances.get(currentAIP);
                    for (BorderlandsObject prevdAIP : prevEvoBalances.keySet()) {
                        BorderlandsArray prevDIPL = prevdAIP.getArrayField("DefaultItemPoolList");
                        if (prevDIPL == null) {
                            continue;
                        }
                        for (int k = 0; k < prevDIPL.size(); k++) {
                            DIPL.add(BorderlandsStruct.parseStruct(prevDIPL.get(k).toString()));//copy, not reference
                            DIPL.get(DIPL.size() - 1).getStruct("PoolProbability").set("BaseValueScaleConstant", fac);
                        }
                    }
                }
                fac++;
            }
        }
    }
}
