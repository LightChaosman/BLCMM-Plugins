package blcmm.plugins.skill_organizer.bin;

import blcmm.data.lib.ApplyablePModel;
import blcmm.data.lib.BorderlandsObject;
import blcmm.data.lib.DataManager;
import blcmm.data.lib.sdk.generated.WillowGame.SkillDefinition;
import blcmm.data.lib.sdk.generated.WillowGame.SkillTreeBranchDefinition;
import blcmm.data.lib.sdk.generated.WillowGame.SkillTreeBranchLayoutDefinition;
import blcmm.model.HotfixType;
import blcmm.model.assist.BLCharacter;
import blcmm.plugins.BLCMMPlugin;
import blcmm.plugins.pseudo_model.PCategory;
import blcmm.plugins.pseudo_model.PHotfix;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

/**
 *
 * @author LightChaosman
 */
class SkillOrganizerPanel extends JPanel {

    private final Map<BLCharacter, Tree> characters = new TreeMap<>();

    {
        Map<BLCharacter, SkillTreeBranchDefinition> map1 = DataManager.streamAllObjectsOfClassAndSubclasses(SkillTreeBranchDefinition.class).
                filter(tree -> tree.Children != null).
                collect(Collectors.toMap(tree -> BLCharacter.find(tree.getFullyQuantizedName()), tree -> tree));

        Map<String, BorderlandsObject> tempCache = DataManager.streamAllDumpsOfClassAndSubclasses("SkillDefinition")
                .map(dump -> BorderlandsObject.parseObject(dump))
                .filter(obj -> !obj.getStringField("SkillIcon").equals("None"))
                .collect(Collectors.toMap(obj -> obj.getName(), obj -> obj));
        ApplyablePModel currentModel = new ApplyablePModel(BLCMMPlugin.getCurrentlyOpenedBLCMMModel());
        map1.forEach((character, original) -> {
            Tree tree = new Tree();
            tree.character = character;
            characters.put(character, tree);
            for (int i = 0; i < 3; i++) {
                BorderlandsObject curBranch1 = BorderlandsObject.parseObject(DataManager.getDump(original.Children[i].toString()));
                currentModel.applyTo(curBranch1);
                SkillTreeBranchDefinition curBranch = SkillTreeBranchDefinition.convert(curBranch1);

                BorderlandsObject layout1 = BorderlandsObject.parseObject(DataManager.getDump(curBranch.Layout.toString()));
                currentModel.applyTo(layout1);
                SkillTreeBranchLayoutDefinition layout = SkillTreeBranchLayoutDefinition.convert(layout1);

                Branch branch = new Branch();
                tree.branches[i] = branch;
                branch.name = curBranch.BranchName;
                branch.objectName = curBranch.getFullyQuantizedName();
                for (int j = 0; j < 6; j++) {
                    for (int k = 0, k1 = 0; k < 3; k++) {
                        if (layout.Tiers[j].bCellIsOccupied[k]) {
                            BorderlandsObject skilldef = tempCache.get(curBranch.Tiers[j].Skills[k1++].object);
                            SkillDefinition def = SkillDefinition.convert(skilldef);
                            Skill skill = new Skill();
                            tree.branches[i].skills[j][k] = skill;
                            skill.name = def.SkillName;
                            skill.icon = def.SkillIcon.object;
                            if (skilldef.containsField("SkillIconTextureName")) {
                                skill.icon += "." + skilldef.getStringField("SkillIconTextureName");
                            }
                            skill.objectName = def.getFullyQuantizedName();
                        }
                    }
                    branch.pointsToNextLevel[j] = curBranch.Tiers[j].PointsToUnlockNextTier;
                }
            }
        });

    }

    public SkillOrganizerPanel() {
        JTabbedPane pane = new JTabbedPane();
        for (BLCharacter x : characters.keySet()) {
            JPanel container = new JPanel();
            container.setLayout(new GridBagLayout());
            container.add(new CharacterPane(characters.get(x)), new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            pane.addTab(x.getCharacterName(), container);
        }
        super.setLayout(new GridBagLayout());
        JLayeredPane layered = new JLayeredPane();
        super.add(layered, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel parentOfPane = new JPanel();
        parentOfPane.setLayout(new GridBagLayout());
        parentOfPane.add(pane, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        layered.add(parentOfPane, new Integer(1));

        JPanel parentOfCheck = new JPanel();
        parentOfCheck.setOpaque(false);
        parentOfCheck.setLayout(new GridBagLayout());
        JCheckBox check = new JCheckBox("Show points needed to progress to next tier");
        check.setHorizontalTextPosition(SwingConstants.LEFT);
        check.addItemListener(e -> {
            for (int i = 0; i < pane.getTabCount(); i++) {
                JPanel panel = (JPanel) pane.getComponentAt(i);
                CharacterPane panel2 = (CharacterPane) panel.getComponent(0);
                panel2.setSpinnersVisible(check.isSelected());
            }
        });
        parentOfCheck.add(check, new GridBagConstraints(1, 1, 1, 1, 1d, 1d, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        layered.add(parentOfCheck, new Integer(2));

        Dimension d = new Dimension(pane.getPreferredSize().width + 3 * CharacterPane.ICON_SIZE, pane.getPreferredSize().height);
        layered.setPreferredSize(d);
        ComponentAdapter adap = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = layered.getSize();
                int w = Math.max(d.width, size.width), h = Math.max(size.height, d.height);
                parentOfPane.setBounds(0, 0, w, h);
                parentOfCheck.setBounds(0, 0, w, h);
            }
        };
        layered.addComponentListener(adap);
        adap.componentResized(null);
    }

    PCategory getResult() {
        PCategory root = new PCategory("Skill Organizer");
        for (Tree tree : characters.values()) {
            if (tree.changed) {
                PCategory vh = new PCategory(tree.character.getCharacterName());
                root.addChild(vh);
                for (Branch branche : tree.branches) {
                    PCategory br = new PCategory(branche.name);
                    vh.addChild(br);
                    for (String com : branche.convertToSetCommands()) {
                        br.addChild(new PHotfix(com, HotfixType.ONDEMAND, tree.character.getStreamingPackage(), "SkillOrganizer"));
                    }
                }
            }
        }
        return root;
    }

    private static class CharacterPane extends JPanel {

        private final static int TREE_PADD = 20;
        private final static int ICON_SIZE = 52;
        private final static int ICON_PADD = 3;
        private static final int T_HEIGHT = 30;

        private final Tree tree;
        private final Map<Rectangle, int[]> rectToCoord = new HashMap<>();
        private final MyAdapter adap;
        private boolean includeSpinners = false;
        private int spinnerPad = 0;

        public CharacterPane(Tree tree) {
            this.tree = tree;
            ToolTipManager.sharedInstance().registerComponent(CharacterPane.this);
            adap = new MyAdapter();
            super.addMouseMotionListener(adap);
            super.addMouseListener(adap);
            super.setLayout(null);
            buildRects();
        }

        public void setSpinnersVisible(boolean b) {
            this.includeSpinners = b;
            buildRects();
            repaint();
        }

        private void buildRects() {
            this.rectToCoord.clear();
            super.removeAll();

            if (includeSpinners) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 6; j++) {
                        JSpinner spinner = new JSpinner(new SpinnerNumberModel(tree.branches[i].pointsToNextLevel[j], 0, 15, 1));
                        Dimension s = spinner.getPreferredSize();
                        int x = getX(i, j, 3);
                        int y = getY(i, j, 3) + ICON_SIZE / 2 - s.height / 2;
                        super.add(spinner);
                        spinner.setBounds(x, y, s.width, s.height);
                        spinnerPad = s.width;
                        final int i2 = i, j2 = j;
                        spinner.addChangeListener(e -> tree.branches[i2].pointsToNextLevel[j2] = (Integer) spinner.getValue());
                    }
                }
            } else {
                spinnerPad = 0;
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++) {
                    for (int k = 0; k < 3; k++) {
                        int x = getX(i, j, k);
                        int y = getY(i, j, k);
                        Rectangle rect = new Rectangle(x, y, ICON_SIZE, ICON_SIZE);
                        rectToCoord.put(rect, new int[]{i, j, k});
                    }
                }
                int x = getX(i, 6, 0);
                int y = getY(i, 6, 0);
                int w = getX(i, 6, includeSpinners ? 4 : 3) - x - 2 * ICON_PADD;
                int h = T_HEIGHT;
                JTextField namefield = new JTextField(tree.branches[i].name);
                super.add(namefield);
                namefield.setBounds(x, y, w, h);
                namefield.setHorizontalAlignment(JTextField.CENTER);
                final int i2 = i;
                namefield.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        tree.branches[i2].name = namefield.getText();
                    }
                });
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(9 * ICON_SIZE + 18 * ICON_PADD + 2 * TREE_PADD + 3 * (includeSpinners ? spinnerPad : 0) + (includeSpinners ? 2 * ICON_PADD : 0),
                    6 * ICON_SIZE + 12 * ICON_PADD + T_HEIGHT + 5);
        }

        private int getX(int i, int j, int k) {
            return i * (TREE_PADD + spinnerPad) + ((3 * i + k) * (ICON_SIZE + ICON_PADD * 2)) + ICON_PADD;
        }

        private int getY(int i, int j, int k) {
            return j * (ICON_SIZE + 2 * ICON_PADD) + ICON_PADD;
        }

        @Override
        public void paint(Graphics grphcs) {
            super.paint(grphcs);
            ((Graphics2D) grphcs).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++) {
                    for (int k = 0; k < 3; k++) {
                        int x = getX(i, j, k);
                        int y = getY(i, j, k);
                        Skill skill = tree.branches[i].skills[j][k];
                        if (skill != null && skill != adap.currentDrag) {
                            if (skill.getIcon() != null) {
                                grphcs.drawImage(skill.getIcon().getImage(), x, y, ICON_SIZE, ICON_SIZE, null);
                            } else {
                                grphcs.setColor(Color.BLACK);
                                grphcs.fillRect(x, y, ICON_SIZE, ICON_SIZE);
                                grphcs.setColor(Color.WHITE);
                                String s1 = "Unknown";
                                String s2 = "Icon";
                                int t1 = grphcs.getFontMetrics().stringWidth(s1);
                                int t2 = grphcs.getFontMetrics().stringWidth(s2);
                                int size = grphcs.getFontMetrics().getHeight();
                                int hoffset = (ICON_SIZE - 2 * size) / 2;
                                grphcs.drawString(s1, x + 2 + (ICON_SIZE - 4 - t1) / 2, y + 1 * size + hoffset - 2);
                                grphcs.drawString(s2, x + 2 + (ICON_SIZE - 4 - t2) / 2, y + 2 * size + hoffset - 2);
                            }
                        }
                    }
                }
            }
            if (adap.currentDrag != null) {
                grphcs.drawImage(adap.currentDrag.getIcon().getImage(), adap.x + adap.xOff, adap.y + adap.yOff, ICON_SIZE, ICON_SIZE, null);
            }
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            Point p = new Point(event.getX(), event.getY());
            for (Rectangle r : rectToCoord.keySet()) {
                if (r.contains(p)) {
                    int[] coords = rectToCoord.get(r);
                    Skill s = tree.branches[coords[0]].skills[coords[1]][coords[2]];
                    if (s != null) {
                        return s.name;
                    } else {
                        return null;
                    }
                }
            }
            return null;
        }

        private class MyAdapter extends MouseAdapter {

            private Skill currentDrag;
            private int[] start;
            int x, y, xOff, yOff;

            @Override
            public void mousePressed(MouseEvent me) {
                for (Rectangle rect : rectToCoord.keySet()) {
                    if (rect.contains(me.getPoint())) {
                        int[] coords = rectToCoord.get(rect);
                        Skill s = tree.branches[coords[0]].skills[coords[1]][coords[2]];
                        if (s != null) {
                            start = coords;
                            currentDrag = s;
                            x = me.getPoint().x;
                            y = me.getPoint().y;
                            xOff = rect.x - x;
                            yOff = rect.y - y;
                        }
                        break;
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                if (currentDrag != null) {
                    x = me.getPoint().x;
                    y = me.getPoint().y;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (currentDrag != null) {
                    for (Rectangle rect : rectToCoord.keySet()) {
                        if (rect.contains(me.getPoint())) {
                            int[] coords = rectToCoord.get(rect);
                            tree.swap(start[0], start[1], start[2], coords[0], coords[1], coords[2]);
                            break;
                        }
                    }
                    currentDrag = null;
                    repaint();
                }
            }
        }
    }
}
