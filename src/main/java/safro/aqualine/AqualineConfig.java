package safro.aqualine;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Aqualine.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AqualineConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_LEVEL = BUILDER
            .comment("Max Fishing Level allowed")
            .defineInRange("maxFishingLevel", 100, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue BASE_ENTITY_CHANCE = BUILDER
            .comment("Base chance to fish an entity instead of an item or event")
            .defineInRange("baseEntityChance", 50, 1, 100);

    private static final ModConfigSpec.IntValue COM_WEIGHT = BUILDER
            .comment("Weight for common fishing loot/mobs. Ideally all rarity weights should add up to 100. Default: 55")
            .defineInRange("commonWeight", 55, 1, 100);

    private static final ModConfigSpec.IntValue UNCOM_WEIGHT = BUILDER
            .comment("Weight for uncommon fishing loot/mobs. Ideally all rarity weights should add up to 100. Default: 30")
            .defineInRange("uncommonWeight", 30, 1, 100);

    private static final ModConfigSpec.IntValue RARE_WEIGHT = BUILDER
            .comment("Weight for rare fishing loot/mobs. Ideally all rarity weights should add up to 100. Default: 13")
            .defineInRange("rareWeight", 13, 1, 100);

    private static final ModConfigSpec.IntValue LEG_WEIGHT = BUILDER
            .comment("Weight for legendary fishing loot/mobs. Ideally all rarity weights should add up to 100. Default: 2")
            .defineInRange("legendaryWeight", 2, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int baseEntityChance;
    public static int maxFishingLevel;
    public static int commonWeight;
    public static int uncommonWeight;
    public static int rareWeight;
    public static int legendaryWeight;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        baseEntityChance = BASE_ENTITY_CHANCE.get();
        maxFishingLevel = MAX_LEVEL.get();
        commonWeight = COM_WEIGHT.get();
        uncommonWeight = UNCOM_WEIGHT.get();
        rareWeight = RARE_WEIGHT.get();
        legendaryWeight = LEG_WEIGHT.get();
    }
}
