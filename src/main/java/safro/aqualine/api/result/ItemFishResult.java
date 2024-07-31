package safro.aqualine.api.result;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemFishResult extends FishResult {
    private final Item item;
    private final int min;
    private final int max;

    public ItemFishResult(Item item, String rarity, int min, int max) {
        super(rarity);
        this.item = item;
        this.min = min;
        this.max = max;
    }

    @Override
    public void execute(ServerLevel world, Player player, FishingHook hook) {
        ItemStack stack = this.getStack(world);
        ItemEntity itementity = new ItemEntity(world, hook.getX(), hook.getY(), hook.getZ(), stack);
        double d0 = player.getX() - hook.getX();
        double d1 = player.getY() - hook.getY();
        double d2 = player.getZ() - hook.getZ();
        itementity.setDeltaMovement(d0 * 0.1, d1 * 0.1 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08, d2 * 0.1);
        world.addFreshEntity(itementity);
        if (stack.is(ItemTags.FISHES)) {
            player.awardStat(Stats.FISH_CAUGHT, 1);
        }
    }

    private ItemStack getStack(Level world) {
        ItemStack stack = new ItemStack(this.item);
        if (this.min < this.max) {
            stack.setCount(Mth.randomBetweenInclusive(world.getRandom(), this.min, this.max));
        }
        return stack;
    }
}
