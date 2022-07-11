package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityMagnetCore extends EntityHasOwner implements IModedEntity {

    public static int life = 100;
    public static int explode_time = 80;

    protected static String TAG_ATTRACT_POWER = "acceleration";
    protected float attractPower = .4F;

    protected static String TAG_DAMAGE = "damage";
    protected float damage = 20F;

    public EntityMagnetCore(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EntityMagnetCore create(Level level, Player player, float damageModifier) {
        EntityMagnetCore core = new EntityMagnetCore(LightlandEntities.MAGNET_CORE.get(), level);
        core.setOwner(player);
        core.setMaxLife(life);
        core.attractPower *= damageModifier;
        core.damage *= damageModifier;
        return core;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(TAG_ATTRACT_POWER, Tag.TAG_FLOAT)) {
            this.attractPower = nbt.getFloat(TAG_ATTRACT_POWER);
        }
        if (nbt.contains(TAG_DAMAGE, Tag.TAG_FLOAT)) {
            this.damage = nbt.getFloat(TAG_DAMAGE);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat(TAG_ATTRACT_POWER, this.attractPower);
        nbt.putFloat(TAG_DAMAGE, this.damage);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(this.attractPower);
        buffer.writeFloat(this.damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf data) {
        super.readSpawnData(data);
        this.attractPower = data.readFloat();
        this.damage = data.readFloat();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount <= explode_time) {
            float r = 8F;
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            List<Entity> list = this.level.getEntities(this, new AABB(x - r, y - r, z - r, x + r, y + r, z + r));
            for (Entity target : list) {
                if (target == this.getOwner())
                    continue;
                if (target instanceof LivingEntity living) {
                    float d = (float) Math.sqrt(Math.pow(living.getX() - x, 2) + Math.pow(living.getY() - y, 2) + Math.pow(living.getZ() - z, 2));
                    float power = Math.max(1 - d / 10F, 0.2F);
                    if (this.tickCount < explode_time) {
                        Vec3 direc = new Vec3(x - living.getX(), y - living.getY(), z - living.getZ()).normalize();
                        living.setDeltaMovement(living.getDeltaMovement().add(direc.scale(this.attractPower * power)));
                    } else {
                        if (this.getOwner() instanceof Player player) {
                            living.hurt(DamageSource.playerAttack(player).setExplosion(), damage);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void powerful() {
        this.damage *= 1.5F;
        this.attractPower *= 1.5F;
    }

    @Override
    public void efficient() {

    }

    @Override
    public void speedup() {
        this.damage *= 0.75F;
        this.attractPower *= 0.75F;
    }
}
