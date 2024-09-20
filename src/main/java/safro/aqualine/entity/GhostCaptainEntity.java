package safro.aqualine.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import safro.aqualine.api.Fishable;
import safro.aqualine.entity.ai.SummonDrownedGoal;

import javax.annotation.Nullable;

public class GhostCaptainEntity extends Monster implements Fishable {
    public static final EntityDataAccessor<Integer> SUMMON_TICKS = SynchedEntityData.defineId(GhostCaptainEntity.class, EntityDataSerializers.INT);

    public GhostCaptainEntity(EntityType<? extends GhostCaptainEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createCaptainAttributes() {
        return createMonsterAttributes().add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.MAX_HEALTH, 50.0).add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.ARMOR, 5.0)
                .add(NeoForgeMod.SWIM_SPEED, 2.0).add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SummonDrownedGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this,0.5, true));
        this.goalSelector.addGoal(5, new GoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        int i = this.entityData.get(SUMMON_TICKS);
        if (i > 0) {
            this.entityData.set(SUMMON_TICKS, i - 1);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isInWater();
    }

    @Override
    public boolean canStandOnFluid(FluidState fluidState) {
        return fluidState.is(FluidTags.WATER);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return this.isInWater() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUMMON_TICKS, 0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterPathNavigation(this, level);
    }

    @Override
    public void onFished(ServerLevel world, Player player) {
        double d = this.getX() - player.getX();
        double e = this.getY() - player.getY();
        double f = this.getZ() - player.getZ();
        player.setDeltaMovement(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean bl = super.doHurtTarget(entity);
        if (entity instanceof LivingEntity target) {
            target.addDeltaMovement(new Vec3(0.0D, -1.2D, 0.0D));
        }
        return bl;
    }

    static class GoToWaterGoal extends MoveToBlockGoal {
        private final GhostCaptainEntity captain;

        GoToWaterGoal(GhostCaptainEntity captain, double speedModifier) {
            super(captain, speedModifier, 8, 2);
            this.captain = captain;
        }

        public BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        public boolean canContinueToUse() {
            return !this.captain.isInWater() && this.isValidTarget(this.captain.level(), this.blockPos);
        }

        public boolean canUse() {
            return !this.captain.isInWater() && super.canUse();
        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos.above()).isPathfindable(PathComputationType.WATER);
        }
    }

    static class WaterPathNavigation extends GroundPathNavigation {
        WaterPathNavigation(GhostCaptainEntity captain, Level level) {
            super(captain, level);
        }

        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }

        protected boolean hasValidPathType(PathType pathType) {
            return pathType == PathType.WATER || pathType == PathType.WATER_BORDER || super.hasValidPathType(pathType);
        }

        public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).is(Blocks.LAVA) || super.isStableDestination(pos);
        }
    }
}
