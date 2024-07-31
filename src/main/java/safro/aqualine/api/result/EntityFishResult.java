package safro.aqualine.api.result;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import safro.aqualine.Aqualine;

public class EntityFishResult extends FishResult {
    private final EntityType<?> entityType;

    public EntityFishResult(EntityType<?> entityType, String rarity) {
        super(rarity);
        this.entityType = entityType;
    }

    @Override
    public void execute(ServerLevel world, Player player, FishingHook hook) {
        Entity entity = entityType.create(world);
        if (entity instanceof LivingEntity living) {
            living.moveTo(hook.getX(), hook.getY() + 1.0, hook.getZ(), player.yHeadRot + 180.0F, 0.0F);
            double d0 = player.getX() - hook.getX();
            double d1 = player.getY() - hook.getY();
            double d2 = player.getZ() - hook.getZ();
            living.setDeltaMovement(d0 * 0.1, d1 * 0.1 + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08, d2 * 0.1);
            if (living instanceof Mob mob) {
                mob.finalizeSpawn(world, world.getCurrentDifficultyAt(hook.blockPosition()), MobSpawnType.NATURAL, null);
            }
            world.addFreshEntity(living);
        } else {
            Aqualine.LOGGER.error("Unable to fish non-living entity of type: " + entityType);
        }
    }
}
