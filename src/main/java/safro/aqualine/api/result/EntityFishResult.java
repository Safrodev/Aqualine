package safro.aqualine.api.result;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import safro.aqualine.Aqualine;
import safro.aqualine.api.Fishable;
import safro.aqualine.entity.projectile.CustomFishingHook;

public class EntityFishResult extends FishResult {
    private final EntityType<?> entityType;

    public EntityFishResult(EntityType<?> entityType, String rarity) {
        super(rarity);
        this.entityType = entityType;
    }

    public EntityFishResult(ResourceLocation id, String rarity) {
        super(rarity);
        this.entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
    }

    public ResourceLocation getEntityId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType);
    }

    @Override
    public ItemStack getDisplayStack() {
        Item egg = SpawnEggItem.byId(this.entityType);
        ItemStack stack = new ItemStack(egg != null ? egg : Items.BARRIER);
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(this.entityType.getDescriptionId()));
        return stack;
    }

    @Override
    public void execute(ServerLevel world, Player player, CustomFishingHook hook) {
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
            if (living instanceof Fishable fishable) {
                fishable.onFished(world, player);
            }
        } else {
            Aqualine.LOGGER.error("Unable to fish non-living entity of type: " + entityType);
        }
    }
}
