package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.content.magic.item.oriental.circle.AbstractCircleMagic;
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

    protected static final EntityDataAccessor<Integer> DEPLOY_TIME = SynchedEntityData.defineId(EntityRoughFireball.class, EntityDataSerializers.INT);

    protected int withdrawTime = 10;

    public EntityCircle(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEPLOY_TIME, 10);
    }

    public static EntityCircle create(Level level, Player player, int deploy, int life) {
        EntityCircle circle = new EntityCircle(LightlandEntities.CIRCLE.get(), level);
        circle.setOwner(player);
        circle.setDeployTime(deploy);
        circle.lifeRemain = life < 0 ? -1 : life + circle.withdrawTime;

        circle.setPos(player.position());
        Vec3 look = player.getLookAngle();
        float xz = (float) Math.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float)(Math.atan2(look.x, look.z) * 180.0D / Math.PI);
        float pitch = (float)(Math.atan2(look.y, xz) * 180.0D / Math.PI);
        circle.setXRot(yaw);
        circle.setYRot(pitch);

        return circle;
    }

    public static EntityCircle create(Level level, Player player) {
        return create(level, player, 10, -1);
    }

    public int getDeployTime() {
        return this.entityData.get(DEPLOY_TIME);
    }

    public void setDeployTime(int time) {
        this.entityData.set(DEPLOY_TIME, time);
    }

    public void setDeployTime(AbstractCircleMagic.CircleMode mode) {
        switch (mode) {
            case SPEEDUP -> setDeployTime((int) (getDeployTime() * 0.75F));
            case EFFICIENT -> setDeployTime((int) (getDeployTime() * 1.5F));
        }
    }

    public int getWithdrawTime() {
        return this.withdrawTime;
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();
        if (this.getOwner() == null) {
            this.withdraw();
            return;
        }
        this.setPos(owner.position());
        this.calcRotation();
        if (owner instanceof Player player) {
            if (!player.isUsingItem()) {
                this.withdraw();
                return;
            }
        } else {
            this.withdraw();
            return;
        }
    }

    public void withdraw() {
        if (this.lifeRemain < 0)
            this.lifeRemain = this.withdrawTime;
        else
            this.lifeRemain = Math.min(this.lifeRemain, this.withdrawTime);
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
