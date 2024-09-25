package safro.aqualine.api.result;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import safro.aqualine.entity.projectile.CustomFishingHook;

public class ItemFishResult extends FishResult {
    public final Item item;
    public final IntProvider range;

    public ItemFishResult(Item item, String rarity, IntProvider range) {
        super(rarity);
        this.item = item;
        this.range = range;
    }

    public IntProvider range() {
        return this.range;
    }

    public Item item() {
        return this.item;
    }

    public ItemStack getDisplayStack() {
        return new ItemStack(this.item);
    }

    @Override
    public void execute(ServerLevel world, Player player, CustomFishingHook hook) {
        ItemStack stack = this.getStack(world);
        runOn(stack, world, player, hook);
    }

    protected static void runOn(ItemStack stack, ServerLevel world, Player player, CustomFishingHook hook) {
        if (hook.getRodStats().has("DoubleChance") && (player.getRandom().nextDouble() * 100) < hook.getRodStats().get("DoubleChance")) {
            stack.setCount(stack.getCount() * 2);
            world.playSound(null, player, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.5F);
        }
        ItemFishedEvent event = new ItemFishedEvent(NonNullList.of(ItemStack.EMPTY, stack), hook.onGround() ? 2 : 1, hook);
        NeoForge.EVENT_BUS.post(event);

        for (ItemStack result : event.getDrops()) {
            ItemEntity itementity = new ItemEntity(world, hook.getX(), hook.getY(), hook.getZ(), result);
            double d0 = player.getX() - hook.getX();
            double d1 = player.getY() - hook.getY();
            double d2 = player.getZ() - hook.getZ();
            itementity.setDeltaMovement(d0 * 0.1, d1 * 0.1 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08, d2 * 0.1);
            world.addFreshEntity(itementity);
            if (result.is(ItemTags.FISHES)) {
                player.awardStat(Stats.FISH_CAUGHT, 1);
            }
        }

        CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, hook, event.getDrops());
    }

    protected ItemStack getStack(Level world) {
        ItemStack stack = new ItemStack(this.item);
        stack.setCount(this.range.sample(world.getRandom()));
        return stack;
    }
}
