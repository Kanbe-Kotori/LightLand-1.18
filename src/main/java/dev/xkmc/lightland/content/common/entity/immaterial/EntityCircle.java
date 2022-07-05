package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityCircle extends EntityHasOwner {

    private static final EntityDataAccessor<Integer> DEPLOY_TIME = SynchedEntityData.defineId(EntityCircle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE_REMAIN = SynchedEntityData.defineId(EntityCircle.class, EntityDataSerializers.INT);

    public EntityCircle(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EntityCircle create(Level level, Player player, int deploy, int life) {
        EntityCircle circle = new EntityCircle(LightlandEntities.CIRCLE.get(), level);
        circle.setOwner(player);

        circle.setPos(player.position());
        Vec3 look = player.getLookAngle();
        float xz = (float) Math.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float)(Math.atan2(look.x, look.z) * 180.0D / Math.PI);
        float pitch = (float)(Math.atan2(look.y, xz) * 180.0D / Math.PI);
        circle.setXRot(yaw);
        circle.setYRot(pitch);

        circle.entityData.set(DEPLOY_TIME, deploy);
        circle.entityData.set(LIFE_REMAIN, life);

        return circle;
    }

    public static EntityCircle create(Level level, Player player) {
        return create(level, player, 10, -1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEPLOY_TIME, 10);
        this.entityData.define(LIFE_REMAIN, -1);
    }

    public int getDeployTime() {
        return this.entityData.get(DEPLOY_TIME);
    }

    public int getRemainTime() {
        return this.entityData.get(LIFE_REMAIN);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entityData.get(LIFE_REMAIN) > 0) {
            this.entityData.set(LIFE_REMAIN, this.entityData.get(LIFE_REMAIN)-1);
        } else if (this.entityData.get(LIFE_REMAIN) == 0) {
            this.kill();
            return;
        }

        Entity owner = this.getOwner();
        if (this.getOwner() == null) {
            this.preKill();
            return;
        }
        this.setPos(owner.position());
        this.calcRotation();
        if (owner instanceof Player player) {
            if (!player.isUsingItem()) {
                this.preKill();
                return;
            }
        } else {
            this.preKill();
            return;
        }
    }

    public void preKill() {
        if (this.entityData.get(LIFE_REMAIN) > 10 || this.entityData.get(LIFE_REMAIN) < 0)
            this.entityData.set(LIFE_REMAIN, 10);
    }

    protected void calcRotation() {
        Vec3 look = getOwner().getLookAngle();
        float xz = (float) Math.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float)(Math.atan2(look.x, look.z) * 180.0D / Math.PI);
        float pitch = (float)(Math.atan2(look.y, xz) * 180.0D / Math.PI);

        this.setXRot(lerpRotation(this.xRotO, yaw));
        this.setYRot(lerpRotation(this.yRotO, pitch));
    }

    protected static float lerpRotation(float p_37274_, float p_37275_) {
        while(p_37275_ - p_37274_ < -180.0F) {
            p_37274_ -= 360.0F;
        }

        while(p_37275_ - p_37274_ >= 180.0F) {
            p_37274_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_37274_, p_37275_);
    }

}
