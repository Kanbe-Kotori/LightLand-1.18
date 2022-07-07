package dev.xkmc.lightland.content.common.entity.immaterial;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class EntityHasOwner extends Entity implements OwnableEntity {

    protected static String TAG_OWNER_UUID = "OwnerUUID";
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(EntityHasOwner.class, EntityDataSerializers.OPTIONAL_UUID);

    protected static String TAG_LIFE_REMAIN = "lifeRemain";
    protected int lifeRemain = -1;

    public EntityHasOwner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void setLife(int life) {
        this.lifeRemain = life;
    }

    public int getRemainLife() {
        return lifeRemain;
    }

    @Override
    public void tick() {
        super.tick();
        if (lifeRemain > 0) {
            lifeRemain--;
        } else if (lifeRemain == 0) {
            if (!level.isClientSide) {
                this.kill();
                return;
            }
        }

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains(TAG_OWNER_UUID, Tag.TAG_STRING)) {
            String uuid = nbt.getString(TAG_OWNER_UUID);
            if (!uuid.equals(""))
                this.setOwnerId(UUID.fromString(uuid));
        }
        if (nbt.contains(TAG_LIFE_REMAIN, Tag.TAG_INT)) {
            this.lifeRemain = nbt.getInt(TAG_LIFE_REMAIN);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        if (this.getOwnerUUID() == null) {
            nbt.putString(TAG_OWNER_UUID, "");
        } else {
            nbt.putString(TAG_OWNER_UUID, this.getOwnerUUID().toString());
        }
        nbt.putInt(TAG_LIFE_REMAIN, this.lifeRemain);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    @Nullable
    @Override
    public Entity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOwner(Player player) {
        setOwnerId(player.getUUID());
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.of(uuid));
    }
}