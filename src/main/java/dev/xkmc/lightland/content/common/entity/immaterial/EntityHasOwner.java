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

    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(EntityHasOwner.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(EntityHasOwner.class, EntityDataSerializers.INT);


    public EntityHasOwner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(LIFE, -1);
    }

    public void setLife(int life) {
        this.entityData.set(LIFE, life);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    @Override
    public void tick() {
        super.tick();
        int life = this.entityData.get(LIFE);
        if (life > 0 && tickCount > life) {
            this.kill();
            return;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        String s = "";
        if (nbt.contains("OwnerUUID", Tag.TAG_STRING)) {
            s = nbt.getString("OwnerUUID");
        }
        if (!s.equals("")) {
            this.setOwnerId(UUID.fromString(s));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        if (this.getOwnerUUID() == null) {
            nbt.putString("OwnerUUID", "");
        } else {
            nbt.putString("OwnerUUID", this.getOwnerUUID().toString());
        }
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