package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
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

public class EntityRoughFireball extends EntityFlying {

    private static final EntityDataAccessor<Integer> CHARGE_TIME = SynchedEntityData.defineId(EntityRoughFireball.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityRoughFireball.class, EntityDataSerializers.FLOAT);

    public EntityRoughFireball(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EntityRoughFireball create(Level level, Player player, int charge, float damage) {
        EntityRoughFireball fireball = new EntityRoughFireball(LightlandEntities.ROUGH_FIREBALL.get(), level);
        fireball.setOwner(player);
        fireball.entityData.set(CHARGE_TIME, charge);
        fireball.entityData.set(DAMAGE, damage);
        fireball.entityData.set(LIFE, charge + 50);
        return fireball;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGE_TIME, 20);
        this.entityData.define(DAMAGE, 0F);
    }

    public int getChargeTime() {
        return this.entityData.get(CHARGE_TIME);
    }

    protected float getGravity() {
        return this.tickCount > this.entityData.get(CHARGE_TIME) ? -0.0002F : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > this.entityData.get(CHARGE_TIME)) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.add(v.normalize().scale(0.01F)));
        } else if (this.tickCount == this.entityData.get(CHARGE_TIME)) {
            this.setDeltaMovement(this.getOwner().getLookAngle().normalize().scale(0.01F));
        } else {
            if (this.getOwner() instanceof Player player) {
                if (!player.isUsingItem()) {
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
            float damage = this.tickCount > this.entityData.get(CHARGE_TIME)? this.entityData.get(DAMAGE) : 0;
            entity.hurt(DamageSource.playerAttack(player), damage);
            entity.setSecondsOnFire((int) damage);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

    }
}
