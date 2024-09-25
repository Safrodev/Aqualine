package safro.aqualine.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.PlayerTeam;
import safro.aqualine.entity.GhostCaptainEntity;

public class SummonSilverfishGoal extends Goal {
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;
    private final GhostCaptainEntity mob;

    public SummonSilverfishGoal(GhostCaptainEntity entity) {
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
    public void tick() {
        --this.attackWarmupDelay;
        if (this.attackWarmupDelay == 0 && this.mob.getTarget() != null) {
            ServerLevel serverlevel = (ServerLevel) this.mob.level();
            PlayerTeam playerteam = this.mob.getTeam();

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
                Silverfish silverfish = EntityType.SILVERFISH.create(this.mob.level());
                if (silverfish != null) {
                    silverfish.moveTo(blockpos, 0.0F, 0.0F);
                    silverfish.finalizeSpawn(serverlevel, this.mob.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null);
                    silverfish.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 3, true, false));
                    silverfish.setTarget(this.mob.getTarget());
                    if (playerteam != null) {
                        serverlevel.getScoreboard().addPlayerToTeam(silverfish.getScoreboardName(), playerteam);
                    }

                    serverlevel.addFreshEntityWithPassengers(silverfish);
                    serverlevel.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(this.mob));
                }
            }
        }
    }
}
