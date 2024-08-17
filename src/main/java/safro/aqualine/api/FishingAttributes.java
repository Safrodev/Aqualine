package safro.aqualine.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;

public class FishingAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Aqualine.MODID);

    public static final Holder<Attribute> FISHING_SPEED = ATTRIBUTES.register("fishing_speed", () -> new RangedAttribute("aqualine.fishing_speed", 1.0, 1.0, 1024.0).setSyncable(true));

    @SubscribeEvent
    public static void applyAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FISHING_SPEED);
    }
}
