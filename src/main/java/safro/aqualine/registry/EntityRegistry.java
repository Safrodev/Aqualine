package safro.aqualine.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;
import safro.aqualine.entity.CustomFishingHook;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Aqualine.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CustomFishingHook>> FISHING_HOOK = ENTITIES.register("fishing_hook", () -> EntityType.Builder.<CustomFishingHook>of(CustomFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("fishing_hook"));
}
