package dev.xkmc.lightland.content.common.entity.immaterial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class EntityFlying extends EntityHasOwner {
    public EntityFlying(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void shoot(double x, double y, double z, float v, float error) {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)error, this.random.nextGaussian() * (double)0.0075F * (double)error, this.random.nextGaussian() * (double)0.0075F * (double)error).scale((double)v);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void updateRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        float xz = (float) Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z);
        float yaw = (float)(Math.atan2(vec3.x, vec3.z) * 180.0D / Math.PI);
        float pitch = (float)(Math.atan2(vec3.y, xz) * 180.0D / Math.PI);

        this.setXRot(lerpRotation(this.xRotO, yaw));
        this.setYRot(lerpRotation(this.yRotO, pitch));
    }

    protected static float lerpRotation(float rot0, float rot) {
        while(rot - rot0 < -180.0F) {
            rot0 -= 360.0F;
        }
        while(rot - rot0 >= 180.0F) {
            rot0 += 360.0F;
        }
        return Mth.lerp(0.2F, rot0, rot);
    }

    @Override
    public void tick() {
        super.tick();
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }

                flag = true;
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS && !flag) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.updateRotation();
        float f;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }
            f = 0.8F;
        } else {
            f = getVDecRate();
        }

        this.setDeltaMovement(vec3.scale(f));
        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
        }

        this.setPos(d2, d0, d1);
    }

    protected boolean canHitEntity(Entity p_37250_) {
        if (!p_37250_.isSpectator() && p_37250_.isAlive() && p_37250_.isPickable()) {
            Entity entity = this.getOwner();
            return entity == null || !entity.isPassengerOfSameVehicle(p_37250_);
        } else {
            return false;
        }
    }

    protected float getGravity() {
        return 0.03F;
    }

    protected float getVDecRate() {
        return 0.99F;
    }

    protected void onHit(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)result);
        } else if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult)result);
        }
        if (type != HitResult.Type.MISS) {
            this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
        }
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }

    protected abstract void onHitEntity(EntityHitResult result);

    protected abstract void onHitBlock(BlockHitResult result);
}
