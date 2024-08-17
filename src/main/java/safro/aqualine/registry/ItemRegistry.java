package safro.aqualine.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingAttributes;
import safro.aqualine.item.AqualineRodItem;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Aqualine.MODID);

    public static final DeferredHolder<Item, AqualineRodItem> STEEL_ROD = ITEMS.register("steel_rod", () -> new AqualineRodItem(withDamage(150).attributes(createAttributes(1, 0))));

    private static Item.Properties withDamage(int dmg) {
        return new Item.Properties().durability(dmg);
    }

    private static ItemAttributeModifiers createAttributes(int speed, int luck) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (speed > 0) {
            builder.add(FishingAttributes.FISHING_SPEED, new AttributeModifier(AqualineRodItem.SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        if (luck > 0) {
            builder.add(Attributes.LUCK, new AttributeModifier(AqualineRodItem.LUCK_ID, luck, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
    }
}
