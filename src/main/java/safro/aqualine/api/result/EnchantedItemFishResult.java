package safro.aqualine.api.result;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.aqualine.entity.CustomFishingHook;

import java.util.Optional;

public class EnchantedItemFishResult extends ItemFishResult {
    private final TagKey<Enchantment> tag;
    private final int levels;

    public EnchantedItemFishResult(Item item, TagKey<Enchantment> tag, int levels, String rarity, int min, int max) {
        super(item, rarity, min, max);
        this.tag = tag;
        this.levels = levels;
    }

    @Override
    public void execute(ServerLevel world, Player player, CustomFishingHook hook) {
        ItemStack stack = this.getStack(world);
        Optional<HolderSet.Named<Enchantment>> optional = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(this.tag);
        ItemStack enchanted = EnchantmentHelper.enchantItem(player.getRandom(), stack, this.levels, world.registryAccess(), optional);
        runOn(enchanted, world, player, hook);
    }
}
