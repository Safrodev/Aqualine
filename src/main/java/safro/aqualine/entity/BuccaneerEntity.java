package safro.aqualine.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;

public class BuccaneerEntity extends Monster implements RangedAttackMob {

    public BuccaneerEntity(EntityType<? extends BuccaneerEntity> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createBuccaneerAttributes() {
        return createMonsterAttributes().add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.MAX_HEALTH, 32.0).add(Attributes.FOLLOW_RANGE, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.ARMOR, 2.0)
                .add(NeoForgeMod.SWIM_SPEED, 2.0).add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DistanceRangedGoal(this, 1.25, 15, 30.0F));
        this.goalSelector.addGoal(1, new DistanceMeleeGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damagesource = this.damageSources().mobAttack(this);
        Level var5 = this.level();
        if (var5 instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        boolean flag = entity.hurt(damagesource, f);
        if (flag) {
            float knockback = Math.max(1.0F - this.getKnockback(entity, damagesource), 0.1F);
            if (entity instanceof LivingEntity livingentity) {
                Vec3 dist = this.position().subtract(livingentity.position()).normalize().scale(knockback);
                livingentity.setDeltaMovement(livingentity.getDeltaMovement().add(dist));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            Level var7 = this.level();
            if (var7 instanceof ServerLevel) {
                ServerLevel serverlevel1 = (ServerLevel) var7;
                EnchantmentHelper.doPostAttackEffects(serverlevel1, entity, damagesource);
            }

            this.setLastHurtMob(entity);
            this.playAttackSound();
        }

        return flag;
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {

    }

    static class DistanceMeleeGoal extends MeleeAttackGoal {
        public DistanceMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = mob.getTarget();
            if (super.canUse()) {
                if (livingEntity != null && livingEntity.isAlive()) {
                    return mob.distanceTo(livingEntity) < 5;
                }
            }
            return false;
        }
    }

    class DistanceRangedGoal extends RangedAttackGoal {
        public DistanceRangedGoal(RangedAttackMob rangedAttackMob, double speedModifier, int attackInterval, float attackRadius) {
            super(rangedAttackMob, speedModifier, attackInterval, attackRadius);
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = BuccaneerEntity.this.getTarget();
            if (super.canUse()) {
                if (livingEntity != null && livingEntity.isAlive()) {
                    return BuccaneerEntity.this.distanceTo(livingEntity) >= 5;
                }
            }
            return false;
        }
    }
}
