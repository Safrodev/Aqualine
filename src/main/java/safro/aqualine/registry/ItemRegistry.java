package safro.aqualine.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingAttributes;
import safro.aqualine.item.AqualineRodItem;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Aqualine.MODID);

    public static final DeferredHolder<Item, AqualineRodItem> STEEL_ROD = ITEMS.register("steel_rod", () -> new AqualineRodItem(withDamage(150).attributes(createAttributes(1, 0))));
    public static final DeferredHolder<Item, AqualineRodItem> FROZEN_ROD = ITEMS.register("frozen_rod", () -> new AqualineRodItem(withDamage(210).attributes(createAttributes(1, 0)), Tags.Biomes.IS_COLD, 2, "text.aqualine.frozen_rod", DyeColor.LIGHT_BLUE.getMapColor().col));
    public static final DeferredHolder<Item, AqualineRodItem> CRYSTAL_ROD = ITEMS.register("crystal_rod", () -> new AqualineRodItem(withDamage(460).attributes(createAttributes(0, 3)), FastColor.ARGB32.color(177, 34, 107)));
    public static final DeferredHolder<Item, AqualineRodItem> UNDEAD_ROD = ITEMS.register("undead_rod", () -> new AqualineRodItem(withDamage(400), null, -1, 30, "text.aqualine.undead_rod", FastColor.ARGB32.color(212, 133, 42)));

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
