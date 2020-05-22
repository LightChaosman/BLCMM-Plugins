package blcmm.plugins.skill_organizer.bin;

import blcmm.data.lib.sdk.core.ObjectReference;
import blcmm.data.lib.sdk.generated.WillowGame.SkillTreeBranchDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author LightChaosman
 */
class Branch {

    Skill[][] skills = new Skill[6][3];
    int[] pointsToNextLevel = new int[6];
    String name;
    String objectName;

    public String[] convertToSetCommands() {
        ArrayList<String> commands = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String skillss = Arrays.stream(skills[i]).filter(s -> s != null).map(s -> "SkillDefinition'" + s.objectName + "'").collect(Collectors.joining(",", "(", ")"));
            commands.add("set " + objectName + " Tiers[" + i + "] (Skills=" + skillss + ",PointsToUnlockNextTier=" + pointsToNextLevel[i] + ")");
        }
        String layoutName = ((SkillTreeBranchDefinition) ObjectReference.parse("SkillTreeBranchDefinition'" + objectName + "'").getReferencedObject()).Layout.object;
        for (int i = 0; i < 6; i++) {
            String skillss = Arrays.stream(skills[i]).map(s -> s == null ? "False" : "True").collect(Collectors.joining(",", "(", ")"));
            commands.add("set " + layoutName + " Tiers[" + i + "] (bCellIsOccupied=" + skillss + ")");
        }
        commands.add("set " + objectName + " BranchName " + name);
        return commands.toArray(new String[commands.size()]);
    }
}
