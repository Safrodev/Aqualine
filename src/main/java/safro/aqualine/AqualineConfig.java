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

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int baseEntityChance;
    public static int maxFishingLevel;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        baseEntityChance = BASE_ENTITY_CHANCE.get();
        maxFishingLevel = MAX_LEVEL.get();
    }
}
