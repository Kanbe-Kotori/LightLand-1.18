package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Random;

public class EntityRainbowOrb extends EntityFlying implements IModedEntity {

    protected static String TAG_TYPE = "type";
    protected int type = 0;

    protected static String TAG_DAMAGE = "damage";
    protected float damage = 3F;

    public EntityRainbowOrb(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static EntityRainbowOrb create(Level level, Player player, float damageModifier) {
        EntityRainbowOrb orb = new EntityRainbowOrb(LightlandEntities.RAINBOW_ORB.get(), level);
        orb.setOwner(player);
        orb.setMaxLife(100);
        orb.damage *= damageModifier;
        orb.type = new Random().nextInt(7);
        return orb;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(TAG_TYPE, Tag.TAG_INT)) {
            this.type = nbt.getInt(TAG_TYPE);
        }
        if (nbt.contains(TAG_DAMAGE, Tag.TAG_FLOAT)) {
            this.damage = nbt.getFloat(TAG_DAMAGE);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt(TAG_TYPE, this.type);
        nbt.putFloat(TAG_DAMAGE, this.damage);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.type);
        buffer.writeFloat(this.damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf data) {
        super.readSpawnData(data);
        this.type = data.readInt();
        this.damage = data.readFloat();
    }

    public int getOrbType() {
        return this.type;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();

        if (this.getOwner() instanceof Player player) {
            switch (this.type) {
                case 0:
                    entity.hurt(DamageSource.playerAttack(player).setIsFire(), damage);
                    entity.setSecondsOnFire((int) damage);
                    break;
                case 1:
                    entity.hurt(DamageSource.playerAttack(player), damage);
                    break;
                case 2:
                    entity.hurt(DamageSource.playerAttack(player).bypassArmor(), damage);
                    break;
                case 3:
                    entity.hurt(DamageSource.playerAttack(player), damage);
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.POISON, (int)(damage * 25)), player);
                    }
                    break;
                case 4:
                    entity.hurt(DamageSource.playerAttack(player), damage);
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, (int)(damage * 20)), player);
                    }
                    break;
                case 5:
                    entity.hurt(DamageSource.playerAttack(player), damage);
                    entity.clearFire();
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int)(damage * 20)), player);
                    }
                    break;
                case 6:
                    entity.hurt(DamageSource.playerAttack(player), damage);
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int)(damage * 20)), player);
                    }
                    break;
            }

        }
        this.kill();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.kill();
    }

    @Override
    public void powerful() {
        this.damage *= 1.5F;
        this.getDeltaMovement().scale(1.5F);
    }

    @Override
    public void efficient() {

    }

    @Override
    public void speedup() {
        this.damage *= 0.75F;
        this.getDeltaMovement().scale(0.75F);
    }
}
