package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityRoughFireball extends EntityFlying implements IModedEntity {

    protected int chargeTime = 20;
    protected int lifeAfterShoot = 50;

    protected static String TAG_ACCELERATION = "acceleration";
    protected float acceleration = .01F;

    protected static String TAG_DAMAGE = "damage";
    protected float damage = 8F;

    public EntityRoughFireball(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EntityRoughFireball create(Level level, Player player, float damageModifier) {
        EntityRoughFireball fireball = new EntityRoughFireball(LightlandEntities.ROUGH_FIREBALL.get(), level);
        fireball.setOwner(player);
        fireball.setMaxLife(fireball.chargeTime + fireball.lifeAfterShoot);
        fireball.damage *= damageModifier;
        return fireball;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(TAG_ACCELERATION, Tag.TAG_FLOAT)) {
            this.acceleration = nbt.getFloat(TAG_ACCELERATION);
        }
        if (nbt.contains(TAG_DAMAGE, Tag.TAG_FLOAT)) {
            this.damage = nbt.getFloat(TAG_DAMAGE);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat(TAG_ACCELERATION, this.acceleration);
        nbt.putFloat(TAG_DAMAGE, this.damage);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.chargeTime);
        buffer.writeFloat(this.acceleration);
        buffer.writeFloat(this.damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf data) {
        super.readSpawnData(data);
        this.chargeTime = data.readInt();
        this.acceleration = data.readFloat();
        this.damage = data.readFloat();
    }

    public int getChargeTime() {
        return this.chargeTime;
    }

    public void powerful() {
        this.damage *= 1.5F;
        this.acceleration *= 1.5F;
    }

    public void efficient() {
        this.chargeTime *= 1.5F;
        this.lifeRemain = this.chargeTime + this.lifeAfterShoot;
    }

    public void speedup() {
        this.damage *= 0.75F;
        this.acceleration *= 0.75F;
        this.chargeTime *= 0.75F;
        this.lifeRemain = this.chargeTime + this.lifeAfterShoot;
    }

    @Override
    protected float getGravity() {
        return this.tickCount > this.chargeTime ? -0.0002F : 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > this.chargeTime) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.add(v.normalize().scale(this.acceleration)));
        } else if (this.tickCount == this.chargeTime) {
            this.setDeltaMovement(this.getOwner().getLookAngle().normalize().scale(this.acceleration));
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
            float actualDamage = this.tickCount > this.chargeTime ? damage : 0;
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
