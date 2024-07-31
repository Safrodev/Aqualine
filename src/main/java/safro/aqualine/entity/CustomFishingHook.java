package safro.aqualine.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;

public class CustomFishingHook extends FishingHook {

    public CustomFishingHook(EntityType<? extends CustomFishingHook> entityType, Level level) {
        super(entityType, level);
    }

    public CustomFishingHook(Player player, Level level, int luck, int lureSpeed) {
        super(player, level, luck, lureSpeed);
    }
}
