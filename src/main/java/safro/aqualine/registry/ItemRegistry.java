package safro.aqualine.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingAttributes;
import safro.aqualine.item.AnchorItem;
import safro.aqualine.item.AqualineRodItem;
import safro.aqualine.item.BaitItem;
import safro.aqualine.item.FishingCharmItem;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Aqualine.MODID);

    // Rods
    public static final DeferredHolder<Item, AqualineRodItem> STEEL_ROD = ITEMS.register("steel_rod", () -> new AqualineRodItem(withDamage(150).attributes(createAttributes(1, 0).build())));
    public static final DeferredHolder<Item, AqualineRodItem> FROZEN_ROD = ITEMS.register("frozen_rod", () -> new AqualineRodItem(withDamage(210).attributes(createAttributes(1, 0).build()), Tags.Biomes.IS_COLD, 2, "text.aqualine.frozen_rod", DyeColor.LIGHT_BLUE.getMapColor().col));
    public static final DeferredHolder<Item, AqualineRodItem> MARSH_ROD = ITEMS.register("marsh_rod", () -> new AqualineRodItem(withDamage(210).attributes(createAttributes(1, 0).add(Attributes.ATTACK_DAMAGE, new AttributeModifier(AqualineRodItem.DAMAGE_ID, 6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()), Tags.Biomes.IS_SWAMP, 3, "text.aqualine.marsh_rod", DyeColor.GREEN.getMapColor().col));
    public static final DeferredHolder<Item, AqualineRodItem> CRYSTAL_ROD = ITEMS.register("crystal_rod", () -> new AqualineRodItem(withDamage(460).attributes(createAttributes(0, 5).build()), FastColor.ARGB32.color(177, 34, 107)));
    public static final DeferredHolder<Item, AqualineRodItem> UNDEAD_ROD = ITEMS.register("undead_rod", () -> new AqualineRodItem(withDamage(400).attributes(createAttributes(2, 0).build()), null, -1, 30, "text.aqualine.undead_rod", FastColor.ARGB32.color(212, 133, 42)));
    public static final DeferredHolder<Item, AqualineRodItem> ANGLER_ROD = ITEMS.register("angler_rod", () -> new AqualineRodItem(withDamage(600).attributes(createAttributes(4, 3).build()).rarity(Rarity.RARE), FastColor.ARGB32.color(26, 109, 63)));
    public static final DeferredHolder<Item, AqualineRodItem> DRAGONFIN_ROD = ITEMS.register("dragonfin_rod", () -> new AqualineRodItem(withDamage(750).attributes(createAttributes(5, 4).build()).rarity(Rarity.EPIC), null, -1, 10, "text.aqualine.dragonfin_rod", FastColor.ARGB32.color(223, 71, 12)));

    // Special Items
    public static final DeferredHolder<Item, AnchorItem> ANCHOR = ITEMS.register("anchor", () -> new AnchorItem(withDamage(125).attributes(AnchorItem.createAttributes())));

    // Charms
    public static final DeferredHolder<Item, FishingCharmItem> LUCKY_CHARM = ITEMS.register("lucky_charm", () -> new FishingCharmItem(oneCount(), 1));
    public static final DeferredHolder<Item, FishingCharmItem> FISHERMAN_CHARM = ITEMS.register("fisherman_charm", () -> new FishingCharmItem(oneCount().rarity(Rarity.UNCOMMON), 2));
    public static final DeferredHolder<Item, FishingCharmItem> POSEIDON_CHARM = ITEMS.register("poseidon_charm", () -> new FishingCharmItem(oneCount().rarity(Rarity.RARE), 4));

    // Baits
    public static final DeferredHolder<Item, BaitItem> SWEET_BAIT = ITEMS.register("sweet_bait", () -> new BaitItem(new Item.Properties(), "text.aqualine.sweet_bait"));
    public static final DeferredHolder<Item, BaitItem> SHADOW_BAIT = ITEMS.register("shadow_bait", () -> new BaitItem(new Item.Properties(), "text.aqualine.shadow_bait"));
    public static final DeferredHolder<Item, BaitItem> SHINY_BAIT = ITEMS.register("shiny_bait", () -> new BaitItem(new Item.Properties(), "text.aqualine.shiny_bait"));
    public static final DeferredHolder<Item, BaitItem> ENCHANTED_BAIT = ITEMS.register("enchanted_bait", () -> new BaitItem(new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true), "text.aqualine.enchanted_bait"));

    // Misc
    public static final DeferredHolder<Item, Item> SEA_STEEL = ITEMS.register("sea_steel", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, SpawnEggItem> BUCCANEER_SPAWN_EGG = ITEMS.register("buccaneer_spawn_egg", () -> new SpawnEggItem(EntityRegistry.BUCCANEER.get(), 0x807355, 0x7488b0, new Item.Properties()));
    public static final DeferredHolder<Item, SpawnEggItem> GHOST_CAPTAIN_SPAWN_EGG = ITEMS.register("ghost_captain_spawn_egg", () -> new SpawnEggItem(EntityRegistry.GHOST_CAPTAIN.get(), 0x7e2626, 0x57d8de, new Item.Properties()));

    private static Item.Properties withDamage(int dmg) {
        return new Item.Properties().durability(dmg);
    }

    private static Item.Properties oneCount() {
        return new Item.Properties().stacksTo(1);
    }

    private static ItemAttributeModifiers.Builder createAttributes(int speed, int luck) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (speed > 0) {
            builder.add(FishingAttributes.FISHING_SPEED, new AttributeModifier(AqualineRodItem.SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        if (luck > 0) {
            builder.add(Attributes.LUCK, new AttributeModifier(AqualineRodItem.LUCK_ID, luck, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        return builder;
    }


}
