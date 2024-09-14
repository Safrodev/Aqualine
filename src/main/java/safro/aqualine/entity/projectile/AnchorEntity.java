package safro.aqualine.entity.projectile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import safro.aqualine.Aqualine;
import safro.aqualine.registry.EntityRegistry;
import safro.aqualine.registry.ItemRegistry;

import javax.annotation.Nullable;

// Rot 90, 0, 90
public class AnchorEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(AnchorEntity.class, EntityDataSerializers.INT);
    @Nullable
    private Entity hooked;
    private int pullTime = 0;

    public AnchorEntity(EntityType<? extends AnchorEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AnchorEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(EntityRegistry.ANCHOR.get(), shooter, level, stack, null);
        this.setOwner(shooter);
        this.setPickupItemStack(stack);
        this.pickup = Pickup.ALLOWED;
    }

    @Override
    public void tick() {
        if (this.hooked != null && this.getOwner() != null) {
            if (this.hooked != this.getOwner() && this.pullTime < 200 && !this.hooked.isRemoved() && this.hooked.level().dimension() == this.level().dimension()) {
                this.pullTime++;
                this.setPos(this.hooked.getX(), this.hooked.getY(0.8), this.hooked.getZ());

                if (this.pullTime > 10) {
                    this.hooked.setDeltaMovement(0.0D, 0.0D, 0.0D);
                    this.pullEntity(this.hooked);
                    this.pullEntity(this);
                } else {
                    this.setDeltaMovement(Vec3.ZERO);
                }
                this.baseTick();
            } else {
                this.setHookedEntity(null);
                super.tick();
            }
        } else {
            super.tick();
        }
    }

    @Override
    protected void applyGravity() {
    }

    private void pullEntity(Entity entity) {
        Entity owner = this.getOwner();
        if (owner != null && entity != owner) {
            if (entity instanceof AnchorEntity anchor) {
                anchor.setNoPhysics(true);
            }

            Vec3 vec3 = owner.getEyePosition().subtract(entity.position());
            entity.setPosRaw(entity.getX(), entity.getY() + vec3.y * 0.05, entity.getZ());
            if (entity.level().isClientSide) {
                entity.yOld = entity.getY();
            }

            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(0.4)));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hit = result.getEntity();
        if (!this.level().isClientSide) {
            this.setHookedEntity(hit);
        }

        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity owner && hit instanceof LivingEntity target) {
            DamageSource source = this.damageSources().mobProjectile(this, owner);
            boolean flag = entity.getType() == EntityType.ENDERMAN;

            owner.setLastHurtMob(target);
            if (target.hurt(source, Mth.ceil(this.getBaseDamage()))) {
                if (!flag) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, this.getWeaponItem());
                    }
                    this.doPostHurtEffects(target);
                }
            } else {
                this.deflect(ProjectileDeflection.REVERSE, target, owner, false);
                this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
                if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                    if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                        this.spawnAtLocation(this.getPickupItem(), 0.1F);
                    }

                    this.discard();
                }
            }
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ItemRegistry.ANCHOR);
    }

    private void setHookedEntity(@Nullable Entity hookedEntity) {
        this.hooked = hookedEntity;
        this.getEntityData().set(DATA_HOOKED_ENTITY, hookedEntity == null ? 0 : hookedEntity.getId() + 1);
        this.pullTime = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HOOKED_ENTITY, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hooked = i > 0 ? this.level().getEntity(i - 1) : null;
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_entity) {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, p_entity, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (this.getOwner() == null) {
            int i = packet.getData();
            Aqualine.LOGGER.error("Failed to recreate anchor on client. {} (id: {}) is not a valid owner.", this.level().getEntity(i), i);
            this.kill();
        }
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    public void playerTouch(Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null) {
            super.playerTouch(entity);
        }
    }
}
