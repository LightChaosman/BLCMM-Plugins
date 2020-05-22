package blcmm.plugins.skill_organizer.bin;

import blcmm.model.assist.BLCharacter;

/**
 *
 * @author LightChaosman
 */
class Tree {

    final Branch[] branches = new Branch[3];
    boolean changed = false;
    BLCharacter character;

    void swap(int branch1, int x1, int y1, int branch2, int x2, int y2) {
        Skill s2 = branches[branch2].skills[x2][y2];
        branches[branch2].skills[x2][y2] = branches[branch1].skills[x1][y1];
        branches[branch1].skills[x1][y1] = s2;
        if (branch1 != branch2 || x1 != x2 || y1 != y2) {
            changed = true;
        }
    }

}
