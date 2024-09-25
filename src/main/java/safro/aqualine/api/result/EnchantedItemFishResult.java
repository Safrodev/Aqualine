package safro.aqualine.api.result;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.aqualine.entity.projectile.CustomFishingHook;

import java.util.Optional;

public class EnchantedItemFishResult extends ItemFishResult {
    private final TagKey<Enchantment> tag;
    private final int levels;

    public EnchantedItemFishResult(Item item, String rarity, IntProvider range, ResourceLocation tag, int levels) {
        super(item, rarity, range);
        this.tag = TagKey.create(Registries.ENCHANTMENT, tag);
        this.levels = levels;
    }

    public ResourceLocation tagKey() {
        return this.tag.location();
    }

    public int levels() {
        return this.levels;
    }

    @Override
    public ItemStack getDisplayStack() {
        ItemStack stack = super.getDisplayStack();
        if (stack.is(Items.BOOK)) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    @Override
    public void execute(ServerLevel world, Player player, CustomFishingHook hook) {
        ItemStack stack = this.getStack(world);
        Optional<HolderSet.Named<Enchantment>> optional = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(this.tag);
        ItemStack enchanted = EnchantmentHelper.enchantItem(player.getRandom(), stack, this.levels, world.registryAccess(), optional);
        runOn(enchanted, world, player, hook);
    }
}
