package blcmm.plugins.timesxspawns;

import blcmm.data.lib.BorderlandsArray;
import blcmm.data.lib.BorderlandsObject;
import blcmm.data.lib.BorderlandsStruct;
import blcmm.data.lib.DataManager;
import blcmm.model.HotfixType;
import blcmm.plugins.pseudo_model.PCategory;
import blcmm.plugins.pseudo_model.PHotfix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class has a method to handle exactly one PopulationOpportunityDen
 *
 * @author LightChaosman
 */
class CodeGenerator implements Consumer<DataManager.Dump> {

    private static final List<String> NPC_ALLEGIANCES = Arrays.asList(new String[]{
        //A hardcoded list of allegienaces that are friendly, hence NPC
        "PawnAllegiance'GD_AI_Allegiance.Allegiance_Player_NoLevel'",
        "PawnAllegiance'GD_AI_Allegiance.Allegiance_Player'",
        "PawnAllegiance'GD_AI_Allegiance.Allegiance_NPCNeutral'",
        "PawnAllegiance'GD_AI_Allegiance.Allegiance_MissionNPC'",
        "PawnAllegiance'GD_AI_Allegiance.Allegiance_FullNeutral'"});
    private final boolean skipBosses;
    private final boolean skipBadasses;
    private final boolean skipFlyers;
    private final boolean skipNPC;
    private final boolean skipTurrets;
    private final int preadd, scale, postadd;
    final PCategory root; //The root that turns our set commands into hotfixes
    int denDone = 0; //counts how many dens are done. Used to update the progress bar
    int skip = 0; //a counter to see how much dens we skip. Not needed for functionality
    HashMap<String, PawnData> pawnmap = new HashMap<>(); //caching the results of the pawn analysis provides a major increase in speed.
    private final TimesXSpawns plugin;

    public CodeGenerator(
            boolean skipBosses,
            boolean skipBadasses,
            boolean skipFlyers,
            boolean skipNPC,
            boolean skipTurrets,
            int preadd,
            int scale,
            int postadd,
            PCategory root,
            TimesXSpawns plugin) {
        this.skipBosses = skipBosses;
        this.skipBadasses = skipBadasses;
        this.skipFlyers = skipFlyers;
        this.skipNPC = skipNPC && false;
        this.root = root;
        this.skipTurrets = skipTurrets;
        this.preadd = preadd;
        this.postadd = postadd;
        this.scale = scale;
        this.plugin = plugin;
    }

    @Override
    public void accept(DataManager.Dump t) {
        String dump = t.dump, objectName = t.object;
        plugin.getProgressBar().setValue(denDone++);
        BorderlandsObject denObject = BorderlandsObject.parseObject(dump, "MaxActiveActorsIsNormal", "MaxActiveActorsThreatened", "MaxTotalActors", "PopulationDef", "Allegiance", "OverrideAllegiance");
        String PopulationDef = denObject.getStringField("PopulationDef");
        if (PopulationDef.equals("None")) {
            // \shrug
            print("skipping " + objectName + " because it does not have a popdef");
            skip++;
            return;
        }
        if (skipNPC) {
            //No need to check allegiance if we're not skipping NPCs... which we always are
            String allegiance1 = denObject.getStringField("Allegiance");
            String allegiance2 = denObject.getStringField("OverrideAllegiance");
            if (NPC_ALLEGIANCES.contains(allegiance1) || NPC_ALLEGIANCES.contains(allegiance2)) {
                print("skipping " + objectName + " it has a friendly allegiance");
                skip++;
                return;
            }
        }
        BorderlandsObject PopulationDefObject = BorderlandsObject.parseObject(DataManager.getDump(PopulationDef), "ActorArchetypeList");
        BorderlandsArray<BorderlandsStruct> ActorArchetypeList = PopulationDefObject.getArrayField("ActorArchetypeList");
        boolean[] allBadass = {true};//Use an array so we can pass the reference down into a method.
        for (int i = 0; i < ActorArchetypeList.size(); i++) {
            String spawnfact = ActorArchetypeList.get(i).getString("SpawnFactory");
            if (!spawnfact.startsWith("PopulationFactoryBalancedAIPawn")) {
                if (spawnfact.startsWith("PopulationFactoryPopulationDefinition")) {
                    //nested factory, assume it's all good
                    continue;
                }
                print("skipping " + objectName + " because spawnfactory (" + spawnfact + ") is not an AIPawn");
                skip++;
                return; // a chest or something.
            }
            BorderlandsObject SpawnFactory = BorderlandsObject.parseObject(DataManager.getDump(spawnfact), "PawnBalanceDefinition", "bIsCriticalActor", "DestructionParams");
            boolean relevant = SpawnFactory.getBoolField("DestructionParams.bActorExemptFromIrrelevancyTests");
            boolean critical = SpawnFactory.getBoolField("bIsCriticalActor");
            String PawnBalanceDefinition = SpawnFactory.getStringField("PawnBalanceDefinition");
            if (PawnBalanceDefinition.equals("None")) {
                print("skipping " + objectName + " because spawnfactory (" + spawnfact + ") does not have a pawn");
                skip++;
                return; // \shrug
            }
            int maxActors = denObject.getIntField("MaxTotalActors");
            if (analyzePawnBalanceDefinition(PawnBalanceDefinition, objectName, relevant, critical, maxActors, allBadass)) {
                return;
            }
        }
        if (allBadass[0] && skipBadasses) {
            print("skipping " + objectName + " because all spawns are badass");
            skip++;
            return;
        }
        int a = denObject.getIntField("MaxActiveActorsIsNormal");
        int b = denObject.getIntField("MaxActiveActorsThreatened");
        int c = denObject.getIntField("MaxTotalActors");
        root.addChild(new PHotfix("set " + objectName + " MaxActiveActorsIsNormal " + compute(a), HotfixType.LEVEL, "None", "xSpawns"));
        root.addChild(new PHotfix("set " + objectName + " MaxActiveActorsThreatened " + compute(b), HotfixType.LEVEL, "None", "xSpawns"));
        root.addChild(new PHotfix("set " + objectName + " MaxTotalActors " + compute(c), HotfixType.LEVEL, "None", "xSpawns"));
    }

    //returns true if there is some reason to skip this pawn
    private boolean analyzePawnBalanceDefinition(String PawnBalanceDefinition, String objectName, boolean relevant, boolean critical, int max, boolean[] allBadass) {
        pawnmap.putIfAbsent(PawnBalanceDefinition, new PawnData());
        pawnmap.get(PawnBalanceDefinition).count++;
        PawnData pawndata = pawnmap.get(PawnBalanceDefinition);
        if (pawndata.count == 1) {
            //actually spend resources to analyze the pawn
            BorderlandsObject PawnBalanceDefinitionObject = BorderlandsObject.parseObject(DataManager.getDump(PawnBalanceDefinition), "AIPawnArchetype", "Champion", "ActorTags");
            pawndata.badass = PawnBalanceDefinitionObject.getBoolField("Champion");
            String dump = DataManager.getDump(PawnBalanceDefinitionObject.getStringField("AIPawnArchetype")).dump;

            BorderlandsObject AIPawnArchetype = BorderlandsObject.parseObject(dump, "Allegiance", "AIClass", "BodyClass");
            BorderlandsObject AIClass = BorderlandsObject.parseObject(DataManager.getDump(AIPawnArchetype.getStringField("AIClass")), "bIsTurret", "Physics", "AIDef", "bBoss", "BehaviorProviderDefinition");

            String allegiance3 = AIPawnArchetype.getStringField("Allegiance");
            pawndata.NPC = NPC_ALLEGIANCES.contains(allegiance3) || PawnBalanceDefinition.contains("NPC");
            pawndata.turret = AIClass.getBoolField("bIsTurret");
            pawndata.flyer = AIClass.getStringField("Physics").equals("PHYS_Flying");

            List<Integer> bossReasons = new ArrayList<>();
            if (PawnBalanceDefinition.toLowerCase().contains("boss") || PawnBalanceDefinition.toLowerCase().contains("raid")) {
                bossReasons.add(0);//Mostly good, some false positives for adds
            }
            if (AIClass.getBoolField("bBoss")) {
                bossReasons.add(1); // A lot of false negatives, no false posivites
            }

            if (pawndata.badass && !pawndata.NPC && max == 1) {
                if (relevant || critical) {
                    bossReasons.add(2);//False positives && False negatives
                    /*
                    It might be better to hard-code a list of enemies to exclude / clasify as bosses,
                    since digging it up with dumps seems a non-trivial task,
                    which takes a lot of resources, which we might not be willing to expend.*/
                }
            }
            pawndata.boss = !bossReasons.isEmpty();
        }
        //use cached data to provide the results
        allBadass[0] = pawnmap.get(PawnBalanceDefinition).badass && allBadass[0];
        if (pawnmap.get(PawnBalanceDefinition).NPC && skipNPC) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " has a friendly allegiance");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition).turret && skipTurrets) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " is a turret");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition).flyer && skipFlyers) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " is a flyer");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition).boss && skipBosses) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " is a boss");
            skip++;
            return true;
        }
        return false;
    }

    private int compute(int original) {
        return (original + preadd) * scale + postadd;
    }

    private void print(Object o) {
        if (false) {//toggleable debug
            System.out.println(o);
        }
    }

    private static class PawnData {

        int count = 0;
        boolean badass = false;
        boolean NPC = false;
        boolean turret = false;
        boolean flyer = false;
        boolean boss = false;
    }

}
