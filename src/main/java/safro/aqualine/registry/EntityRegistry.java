package safro.aqualine.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;
import safro.aqualine.entity.BuccaneerEntity;
import safro.aqualine.entity.projectile.AnchorEntity;
import safro.aqualine.entity.projectile.CustomFishingHook;

@EventBusSubscriber(modid = Aqualine.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Aqualine.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CustomFishingHook>> FISHING_HOOK = ENTITIES.register("fishing_hook", () -> EntityType.Builder.<CustomFishingHook>of(CustomFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("fishing_hook"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnchorEntity>> ANCHOR = ENTITIES.register("anchor", () -> EntityType.Builder.<AnchorEntity>of(AnchorEntity::new, MobCategory.MISC).noSave().sized(0.5F, 0.5F).clientTrackingRange(8).build("anchor"));

    // Mobs
    public static final DeferredHolder<EntityType<?>, EntityType<BuccaneerEntity>> BUCCANEER = ENTITIES.register("headless_buccaneer", () -> EntityType.Builder.of(BuccaneerEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8).build("headless_buccaneer"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(BUCCANEER.get(), BuccaneerEntity.createBuccaneerAttributes().build());
    }
}
