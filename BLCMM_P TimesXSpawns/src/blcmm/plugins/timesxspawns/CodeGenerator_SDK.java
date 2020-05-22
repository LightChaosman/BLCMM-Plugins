package blcmm.plugins.timesxspawns;

import blcmm.data.lib.sdk.core.ObjectCache;
import blcmm.data.lib.sdk.core.ObjectReference;
import blcmm.data.lib.sdk.generated.Engine.Actor;
import blcmm.data.lib.sdk.generated.GearboxFramework.PopulationDefinition;
import blcmm.data.lib.sdk.generated.GearboxFramework.PopulationFactory;
import blcmm.data.lib.sdk.generated.WillowGame.AIClassDefinition;
import blcmm.data.lib.sdk.generated.WillowGame.AIPawnBalanceDefinition;
import blcmm.data.lib.sdk.generated.WillowGame.PopulationFactoryBalancedAIPawn;
import blcmm.data.lib.sdk.generated.WillowGame.PopulationOpportunityDen;
import blcmm.data.lib.sdk.generated.WillowGame.WillowAIPawn;
import blcmm.data.lib.sdk.generated.WillowGame.WillowPopulationDefinition;
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
class CodeGenerator_SDK implements Consumer<PopulationOpportunityDen> {

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

    public CodeGenerator_SDK(
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

        ObjectCache.registerClass(WillowPopulationDefinition.class);
        ObjectCache.registerClass(PopulationFactoryBalancedAIPawn.class);
        ObjectCache.registerClass(WillowAIPawn.class);
        ObjectCache.registerClass(AIClassDefinition.class);

        ObjectCache.getStream(WillowPopulationDefinition.class);
        ObjectCache.getStream(PopulationFactoryBalancedAIPawn.class);
        ObjectCache.getStream(WillowAIPawn.class);
        ObjectCache.getStream(AIClassDefinition.class);
    }

    @Override
    public void accept(PopulationOpportunityDen den) {
        plugin.getProgressBar().setValue(denDone++);
        //System.err.println("starting " + den.getFullyQuantizedName());
        if (skipNPC) {//No need to check allegiance if we're not skipping NPCs
            if (NPC_ALLEGIANCES.contains(den.Allegiance.toString()) || NPC_ALLEGIANCES.contains(den.OverrideAllegiance.toString())) {
                print("skipping " + den.Name + " it has a friendly allegiance");
                skip++;
                return;
            }
        }
        if (den.PopulationDef == ObjectReference.NONE) {
            print("skipping " + den.Name + " because it does not have a popdef");
            skip++;
            return;
        }
        PopulationDefinition PopulationDef = den.PopulationDef.getReferencedObject();
        boolean[] allBadass = {true};//Use an array so we can pass the reference down into a method.
        for (int i = 0; i < PopulationDef.ActorArchetypeList.length; i++) {
            ObjectReference<PopulationFactory> ref = PopulationDef.ActorArchetypeList[i].SpawnFactory;
            if (!ref.clas.equals("PopulationFactoryBalancedAIPawn")) {
                if (ref.clas.equals("PopulationFactoryPopulationDefinition")) {
                    //nested factory, assume it's all good
                    continue;
                }
                print("skipping " + den.Name + " because spawnfactory (" + ref + ") is not an AIPawn");
                skip++;
                return; // a chest or something.
            }
            PopulationFactoryBalancedAIPawn SpawnFactory = (PopulationFactoryBalancedAIPawn) ref.getReferencedObject();
            if (SpawnFactory.PawnBalanceDefinition == ObjectReference.NONE) {
                print("skipping " + den.Name + " because spawnfactory (" + ref + ") does not have a pawn");
                skip++;
                return; // \shrug
            }
            if (analyzePawnBalanceDefinition(
                    SpawnFactory.PawnBalanceDefinition,
                    den.Name,
                    SpawnFactory.DestructionParams.bActorExemptFromIrrelevancyTests,
                    SpawnFactory.bIsCriticalActor, den.MaxTotalActors, allBadass)) {
                return;
            }
        }
        if (allBadass[0] && skipBadasses) {
            print("skipping " + den.Name + " because all spawns are badass");
            skip++;
            return;
        }
        root.addChild(new PHotfix("set " + den.getFullyQuantizedName() + " MaxActiveActorsIsNormal " + compute(den.MaxActiveActorsIsNormal), HotfixType.LEVEL, "None", "xSpawns"));
        root.addChild(new PHotfix("set " + den.getFullyQuantizedName() + " MaxActiveActorsThreatened " + compute(den.MaxActiveActorsThreatened), HotfixType.LEVEL, "None", "xSpawns"));
        root.addChild(new PHotfix("set " + den.getFullyQuantizedName() + " MaxTotalActors " + compute(den.MaxTotalActors), HotfixType.LEVEL, "None", "xSpawns"));
    }

    //returns true if there is some reason to skip this pawn
    private boolean analyzePawnBalanceDefinition(ObjectReference<AIPawnBalanceDefinition> PawnBalanceDefinition, String objectName, boolean relevant, boolean critical, int max, boolean[] allBadass) {
        pawnmap.putIfAbsent(PawnBalanceDefinition.toString(), new PawnData());
        pawnmap.get(PawnBalanceDefinition.toString()).count++;
        PawnData pawndata = pawnmap.get(PawnBalanceDefinition.toString());
        if (pawndata.count == 1) {
            //actually spend resources to analyze the pawn
            AIPawnBalanceDefinition PawnBalanceDefinitionObject = PawnBalanceDefinition.getReferencedObject();
            pawndata.badass = PawnBalanceDefinitionObject.Champion;

            WillowAIPawn AIPawnArchetype = PawnBalanceDefinitionObject.AIPawnArchetype.getReferencedObject();
            AIClassDefinition AIClass = AIPawnArchetype.AIClass.getReferencedObject();

            pawndata.NPC = NPC_ALLEGIANCES.contains(AIPawnArchetype.Allegiance.toString()) || PawnBalanceDefinition.object.contains("NPC");
            pawndata.turret = AIClass.bIsTurret;
            pawndata.flyer = AIClass.Physics == Actor.EPhysics.PHYS_Flying;

            List<Integer> bossReasons = new ArrayList<>();
            if (PawnBalanceDefinition.object.toLowerCase().contains("boss") || PawnBalanceDefinition.object.toLowerCase().contains("raid")) {
                bossReasons.add(0);//Mostly good, some false positives for adds
            }
            if (AIClass.bBoss) {
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
        allBadass[0] = pawnmap.get(PawnBalanceDefinition.toString()).badass && allBadass[0];
        if (pawnmap.get(PawnBalanceDefinition.toString()).NPC && skipNPC) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " has a friendly allegiance");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition.toString()).turret && skipTurrets) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " is a turret");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition.toString()).flyer && skipFlyers) {
            print("skipping " + objectName + " its pawn " + PawnBalanceDefinition + " is a flyer");
            skip++;
            return true;
        }
        if (pawnmap.get(PawnBalanceDefinition.toString()).boss && skipBosses) {
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
