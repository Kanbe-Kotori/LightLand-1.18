package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityRoughFireball extends EntityFlying implements IModedEntity {

    protected static final EntityDataAccessor<Integer> CHARGE_TIME = SynchedEntityData.defineId(EntityRoughFireball.class, EntityDataSerializers.INT);
    protected int lifeAfterShoot = 50;

    protected static String TAG_ACCELERATION = "acceleration";
    protected static final EntityDataAccessor<Float> ACCELERATION = SynchedEntityData.defineId(EntityRoughFireball.class, EntityDataSerializers.FLOAT);

    protected static String TAG_DAMAGE = "damage";
    protected float damage = 8F;

    public EntityRoughFireball(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGE_TIME, 20);
        this.entityData.define(ACCELERATION, 0.01F);
    }

    public static EntityRoughFireball create(Level level, Player player, float damageModifier) {
        EntityRoughFireball fireball = new EntityRoughFireball(LightlandEntities.ROUGH_FIREBALL.get(), level);
        fireball.setOwner(player);
        fireball.lifeRemain = fireball.getChargeTime() + fireball.lifeAfterShoot;
        fireball.damage *= damageModifier;
        return fireball;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(TAG_DAMAGE, Tag.TAG_FLOAT)) {
            this.damage = nbt.getFloat(TAG_DAMAGE);
        }
        if (nbt.contains(TAG_ACCELERATION, Tag.TAG_FLOAT)) {
            this.setAcc(nbt.getFloat(TAG_ACCELERATION));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat(TAG_DAMAGE, this.damage);
        nbt.putFloat(TAG_ACCELERATION, this.getAcc());
    }

    public void powerful() {
        this.damage *= 1.5F;
        this.setAcc(this.getAcc() * 1.5F);
    }

    public void efficient() {
        this.setChargeTime((int) (this.getChargeTime() * 1.5F));
        this.lifeRemain = this.getChargeTime() + this.lifeAfterShoot;
    }

    public void speedup() {
        this.damage *= 0.75F;
        this.setAcc(this.getAcc() * 0.75F);
        this.setChargeTime((int) (this.getChargeTime() * 0.75F));
        this.lifeRemain = this.getChargeTime() + this.lifeAfterShoot;
    }

    public int getChargeTime() {
        return this.entityData.get(CHARGE_TIME);
    }

    public void setChargeTime(int chargeTime) {
        this.entityData.set(CHARGE_TIME, chargeTime);
    }

    public float getAcc() {
        return this.entityData.get(ACCELERATION);
    }

    public void setAcc(float acc) {
        this.entityData.set(ACCELERATION, acc);
    }

    protected float getGravity() {
        return this.tickCount > getChargeTime() ? -0.0002F : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > getChargeTime()) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.add(v.normalize().scale(getAcc())));
        } else if (this.tickCount == getChargeTime()) {
            this.setDeltaMovement(this.getOwner().getLookAngle().normalize().scale(getAcc()));
        } else {
            if (this.getOwner() instanceof Player player) {
                if (!player.isUsingItem() && !level.isClientSide) {
                    this.kill();
                    return;
                } else {
                    Vec3 xAxis = player.getLookAngle().normalize();
                    Vec3 pos = player.position()
                            .add(0, player.getEyeHeight(), 0)
                            .add(xAxis.scale(2));
                    this.setPos(pos);
                }
            } else {
                this.kill();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (this.getOwner() instanceof Player player) {
            float actualDamage = this.tickCount > getChargeTime() ? damage : 0;
            entity.hurt(DamageSource.playerAttack(player).setIsFire(), actualDamage);
            entity.setSecondsOnFire((int) actualDamage);
        }
        this.kill();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.kill();
    }
}
