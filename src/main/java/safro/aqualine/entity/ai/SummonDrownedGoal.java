package safro.aqualine.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.PlayerTeam;
import safro.aqualine.entity.GhostCaptainEntity;

public class SummonDrownedGoal extends Goal {
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;
    private final GhostCaptainEntity mob;

    public SummonDrownedGoal(GhostCaptainEntity entity) {
        this.mob = entity;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = mob.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            return mob.tickCount >= this.nextAttackTickCount;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = mob.getTarget();
        return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
    }

    @Override
    public void start() {
        this.attackWarmupDelay = this.adjustedTickDelay(20);
        mob.getEntityData().set(GhostCaptainEntity.SUMMON_TICKS, 50);
        this.nextAttackTickCount = mob.tickCount + 240;
        mob.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
    }

    @Override
    public void stop() {
//        this.mob.getAttribute(Attributes.ARMOR).removeModifier()
    }

    @Override
    public void tick() {
        --this.attackWarmupDelay;
        if (this.attackWarmupDelay == 0 && this.mob.getTarget() != null) {
            ServerLevel serverlevel = (ServerLevel) this.mob.level();
            PlayerTeam playerteam = this.mob.getTeam();

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
                Drowned drowned = EntityType.DROWNED.create(this.mob.level());
                if (drowned != null) {
                    drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                    drowned.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
                    drowned.setBaby(false);
                    drowned.moveTo(blockpos, 0.0F, 0.0F);
                    drowned.finalizeSpawn(serverlevel, this.mob.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null);
                    drowned.setTarget(this.mob.getTarget());
                    if (playerteam != null) {
                        serverlevel.getScoreboard().addPlayerToTeam(drowned.getScoreboardName(), playerteam);
                    }

                    serverlevel.addFreshEntityWithPassengers(drowned);
                    serverlevel.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(this.mob));
                }
            }
        }
    }
}
