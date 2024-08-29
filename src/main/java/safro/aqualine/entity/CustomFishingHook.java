package safro.aqualine.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.EventHooks;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingManager;
import safro.aqualine.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.util.Collections;

// Reimplementation of FishingHook with custom features. Extends FishingHook for compat purposes
public class CustomFishingHook extends FishingHook {
    private final RandomSource syncronizedRandom;
    private boolean biting;
    private int outOfWaterTime;
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(CustomFishingHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BITING = SynchedEntityData.defineId(CustomFishingHook.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LINE_COLOR = SynchedEntityData.defineId(CustomFishingHook.class, EntityDataSerializers.INT);
    private int life;
    private int nibble;
    private int timeUntilLured;
    private int timeUntilHooked;
    private float fishAngle;
    private boolean openWater;
    @Nullable
    private Entity hookedIn;
    private FishHookState currentState;
    public final int luck;
    private final int lureSpeed;
    public final int entityBonus;
    public final boolean doubled;
    public int lineColor;

    private CustomFishingHook(EntityType<? extends FishingHook> entityType, Level level, int luck, int lureSpeed, int entityBonus, boolean doubled, int color) {
        super(entityType, level);
        this.syncronizedRandom = RandomSource.create();
        this.openWater = true;
        this.currentState = FishHookState.FLYING;
        this.noCulling = true;
        this.luck = Math.max(0, luck);
        this.lureSpeed = Math.max(0, lureSpeed);
        this.entityBonus = Math.max(0, entityBonus);
        this.doubled = doubled;
        this.lineColor = color;
    }

    public CustomFishingHook(EntityType<? extends CustomFishingHook> entityType, Level level) {
        this(entityType, level, 0, 0, 0, false, FastColor.ARGB32.color(0, 0, 0));
    }

    public CustomFishingHook(Player player, Level level, int luck, int lureSpeed, int entityBonus, boolean doubled, int color) {
        this(EntityRegistry.FISHING_HOOK.get(), level, luck, lureSpeed, entityBonus, doubled, color);
        this.setOwner(player);
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        double d0 = player.getX() - (double)f3 * 0.3;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - (double)f2 * 0.3;
        this.moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
        double d3 = vec3.length();
        vec3 = vec3.multiply(0.6 / d3 + this.random.triangle(0.5, 0.0103365), 0.6 / d3 + this.random.triangle(0.5, 0.0103365), 0.6 / d3 + this.random.triangle(0.5, 0.0103365));
        this.setDeltaMovement(vec3);
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0 / 3.1415927410125732));
        this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0 / 3.1415927410125732));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HOOKED_ENTITY, 0);
        builder.define(DATA_BITING, false);
        builder.define(LINE_COLOR, this.lineColor);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level().getEntity(i - 1) : null;
        }

        if (DATA_BITING.equals(key)) {
            this.biting = this.getEntityData().get(DATA_BITING);
            if (this.biting) {
                this.setDeltaMovement(this.getDeltaMovement().x, -0.4F * Mth.nextFloat(this.syncronizedRandom, 0.6F, 1.0F), this.getDeltaMovement().z);
            }
        }

        if (LINE_COLOR.equals(key)) {
            this.lineColor = this.getEntityData().get(LINE_COLOR);
        }

        // From Entity.class
        if (DATA_POSE.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }

    public void tick() {
        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
        this.baseTick(); // avoid calling FishingHook::tick
        if (!this.level().isClientSide) {
            this.getEntityData().set(LINE_COLOR, this.lineColor);
        }

        Player player = this.getPlayerOwner();
        if (player == null) {
            this.discard();
        } else if (this.level().isClientSide || !this.shouldStopFishing(player)) {
            if (this.onGround()) {
                ++this.life;
                if (this.life >= 1200) {
                    this.discard();
                    return;
                }
            } else {
                this.life = 0;
            }

            float f = 0.0F;
            BlockPos blockpos = this.blockPosition();
            FluidState fluidstate = this.level().getFluidState(blockpos);
            if (fluidstate.is(FluidTags.WATER)) {
                f = fluidstate.getHeight(this.level(), blockpos);
            }

            boolean flag = f > 0.0F;
            if (this.currentState == FishHookState.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.currentState = FishHookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
                    this.currentState = FishHookState.BOBBING;
                    return;
                }

                this.checkCollision();
            } else {
                if (this.currentState == FishHookState.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                        } else {
                            this.setHookedEntity(null);
                            this.currentState = FishHookState.FLYING;
                        }
                    }

                    return;
                }

                if (this.currentState == FishHookState.BOBBING) {
                    Vec3 vec3 = this.getDeltaMovement();
                    double d0 = this.getY() + vec3.y - (double)blockpos.getY() - (double)f;
                    if (Math.abs(d0) < 0.01) {
                        d0 += Math.signum(d0) * 0.1;
                    }

                    this.setDeltaMovement(vec3.x * 0.9, vec3.y - d0 * (double)this.random.nextFloat() * 0.2, vec3.z * 0.9);
                    if (this.nibble <= 0 && this.timeUntilHooked <= 0) {
                        this.openWater = true;
                    } else {
                        this.openWater = this.openWater && this.outOfWaterTime < 10 && this.calculateOpenWater(blockpos);
                    }

                    if (flag) {
                        this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                        if (this.biting) {
                            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.1 * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0));
                        }

                        if (!this.level().isClientSide) {
                            this.catchingFish(blockpos);
                        }
                    } else {
                        this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
                    }
                }
            }

            if (!fluidstate.is(FluidTags.WATER)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            this.updateRotation();
            if (this.currentState == FishHookState.FLYING && (this.onGround() || this.horizontalCollision)) {
                this.setDeltaMovement(Vec3.ZERO);
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
            this.reapplyPosition();
        }

    }

    private boolean shouldStopFishing(Player player) {
        ItemStack itemstack = player.getMainHandItem();
        ItemStack itemstack1 = player.getOffhandItem();
        boolean flag = itemstack.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
        boolean flag1 = itemstack1.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
        if (!player.isRemoved() && player.isAlive() && (flag || flag1) && !(this.distanceToSqr(player) > 1024.0)) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    private void checkCollision() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() == HitResult.Type.MISS || !EventHooks.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

    }

    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) || target.isAlive() && target instanceof ItemEntity;
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.setHookedEntity(result.getEntity());
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(result.distanceTo(this)));
    }

    private void setHookedEntity(@Nullable Entity hookedEntity) {
        this.hookedIn = hookedEntity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, hookedEntity == null ? 0 : hookedEntity.getId() + 1);
    }

    private void catchingFish(BlockPos pos) {
        ServerLevel serverlevel = (ServerLevel)this.level();
        int i = 1;
        BlockPos blockpos = pos.above();
        if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(blockpos)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(blockpos)) {
            --i;
        }

        if (this.nibble > 0) {
            --this.nibble;
            if (this.nibble <= 0) {
                this.timeUntilLured = 0;
                this.timeUntilHooked = 0;
                this.getEntityData().set(DATA_BITING, false);
            }
        } else {
            float f5;
            float f6;
            float f7;
            double d4;
            double d5;
            double d6;
            BlockState blockstate1;
            if (this.timeUntilHooked > 0) {
                this.timeUntilHooked -= i;
                if (this.timeUntilHooked > 0) {
                    this.fishAngle += (float)this.random.triangle(0.0, 9.188);
                    f5 = this.fishAngle * 0.017453292F;
                    f6 = Mth.sin(f5);
                    f7 = Mth.cos(f5);
                    d4 = this.getX() + (double)(f6 * (float)this.timeUntilHooked * 0.1F);
                    d5 = (float)Mth.floor(this.getY()) + 1.0F;
                    d6 = this.getZ() + (double)(f7 * (float)this.timeUntilHooked * 0.1F);
                    blockstate1 = serverlevel.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (blockstate1.is(Blocks.WATER)) {
                        if (this.random.nextFloat() < 0.15F) {
                            serverlevel.sendParticles(ParticleTypes.BUBBLE, d4, d5 - 0.10000000149011612, d6, 1, f6, 0.1, f7, 0.0);
                        }

                        float f3 = f6 * 0.04F;
                        float f4 = f7 * 0.04F;
                        serverlevel.sendParticles(ParticleTypes.FISHING, d4, d5, d6, 0, f4, 0.01, -f3, 1.0);
                        serverlevel.sendParticles(ParticleTypes.FISHING, d4, d5, d6, 0, -f4, 0.01, f3, 1.0);
                    }
                } else {
                    this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getY() + 0.5;
                    serverlevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                    serverlevel.sendParticles(ParticleTypes.FISHING, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0, this.getBbWidth(), 0.20000000298023224);
                    this.nibble = Mth.nextInt(this.random, 20, 40);
                    this.getEntityData().set(DATA_BITING, true);
                }
            } else if (this.timeUntilLured > 0) {
                this.timeUntilLured -= i;
                f5 = 0.15F;
                if (this.timeUntilLured < 20) {
                    f5 += (float)(20 - this.timeUntilLured) * 0.05F;
                } else if (this.timeUntilLured < 40) {
                    f5 += (float)(40 - this.timeUntilLured) * 0.02F;
                } else if (this.timeUntilLured < 60) {
                    f5 += (float)(60 - this.timeUntilLured) * 0.01F;
                }

                if (this.random.nextFloat() < f5) {
                    f6 = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f7 = Mth.nextFloat(this.random, 25.0F, 60.0F);
                    d4 = this.getX() + (double)(Mth.sin(f6) * f7) * 0.1;
                    d5 = (float)Mth.floor(this.getY()) + 1.0F;
                    d6 = this.getZ() + (double)(Mth.cos(f6) * f7) * 0.1;
                    blockstate1 = serverlevel.getBlockState(BlockPos.containing(d4, d5 - 1.0, d6));
                    if (blockstate1.is(Blocks.WATER)) {
                        serverlevel.sendParticles(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                    }
                }

                if (this.timeUntilLured <= 0) {
                    this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
                    this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
                }
            } else {
                this.timeUntilLured = Mth.nextInt(this.random, 200, 650);
                this.timeUntilLured -= this.lureSpeed;
            }
        }

    }

    private boolean calculateOpenWater(BlockPos pos) {
        OpenWaterType fishinghook$openwatertype = OpenWaterType.INVALID;

        for(int i = -1; i <= 2; ++i) {
            OpenWaterType fishinghook$openwatertype1 = this.getOpenWaterTypeForArea(pos.offset(-2, i, -2), pos.offset(2, i, 2));
            switch (fishinghook$openwatertype1.ordinal()) {
                case 0:
                    if (fishinghook$openwatertype == OpenWaterType.INVALID) {
                        return false;
                    }
                    break;
                case 1:
                    if (fishinghook$openwatertype == OpenWaterType.ABOVE_WATER) {
                        return false;
                    }
                    break;
                case 2:
                    return false;
            }

            fishinghook$openwatertype = fishinghook$openwatertype1;
        }

        return true;
    }

    private OpenWaterType getOpenWaterTypeForArea(BlockPos firstPos, BlockPos secondPos) {
        return BlockPos.betweenClosedStream(firstPos, secondPos).map(this::getOpenWaterTypeForBlock).reduce((p_37139_, p_37140_) -> {
            return p_37139_ == p_37140_ ? p_37139_ : OpenWaterType.INVALID;
        }).orElse(OpenWaterType.INVALID);
    }

    private OpenWaterType getOpenWaterTypeForBlock(BlockPos pos) {
        BlockState blockstate = this.level().getBlockState(pos);
        if (!blockstate.isAir() && !blockstate.is(Blocks.LILY_PAD)) {
            FluidState fluidstate = blockstate.getFluidState();
            return fluidstate.is(FluidTags.WATER) && fluidstate.isSource() && blockstate.getCollisionShape(this.level(), pos).isEmpty() ? OpenWaterType.INSIDE_WATER : OpenWaterType.INVALID;
        } else {
            return OpenWaterType.ABOVE_WATER;
        }
    }

    public boolean isOpenWaterFishing() {
        return this.openWater;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("LineColor", this.lineColor);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        this.lineColor = compound.getInt("LineColor");
    }

    public int retrieve(ItemStack stack) {
        Player player = this.getPlayerOwner();
        if (!this.level().isClientSide && player != null && !this.shouldStopFishing(player)) {
            int i = 0;
            if (this.hookedIn != null) {
                this.pullEntity(this.hookedIn);
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, stack, this, Collections.emptyList());
                this.level().broadcastEntityEvent(this, (byte)31);
                i = this.hookedIn instanceof ItemEntity ? 3 : 5;
            } else if (this.nibble > 0) {
                FishingManager.handleRandomFish((ServerLevel) this.level(), player, this);
                i = 1;
            }

            if (this.onGround()) {
                i = 2;
            }

            this.discard();
            return i;
        } else {
            return 0;
        }
    }

    public void handleEntityEvent(byte id) {
        if (id == 31 && this.level().isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
            this.pullEntity(this.hookedIn);
        }

        super.handleEntityEvent(id);
    }

    protected void pullEntity(Entity p_entity) {
        Entity entity = this.getOwner();
        if (entity != null) {
            Vec3 vec3 = (new Vec3(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).scale(0.1);
            p_entity.setDeltaMovement(p_entity.getDeltaMovement().add(vec3));
        }

    }

    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public void remove(Entity.RemovalReason reason) {
        this.updateOwnerInfo(null);
        super.remove(reason);
    }

    public void onClientRemoval() {
        this.updateOwnerInfo(null);
    }

    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.updateOwnerInfo(this);
    }

    private void updateOwnerInfo(@Nullable CustomFishingHook fishingHook) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            player.fishing = fishingHook;
        }

    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_entity) {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, p_entity, entity == null ? this.getId() : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (this.getPlayerOwner() == null) {
            int i = packet.getData();
            Aqualine.LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level().getEntity(i), i);
            this.kill();
        }

    }

    enum FishHookState {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;

        FishHookState() {
        }
    }

    enum OpenWaterType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;

        OpenWaterType() {
        }
    }
}
