package dev.xkmc.lightland.content.common.entity.immaterial;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntityHasOwner extends Entity implements OwnableEntity, IEntityAdditionalSpawnData {

    protected static String TAG_OWNER_UUID = "OwnerUUID";
    public String ownerUUID = "";

    protected static String TAG_LIFE_REMAIN = "lifeRemain";
    protected int lifeRemain = -1;

    public EntityHasOwner(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {}

    public void setMaxLife(int life) {
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
        } else if (tickCount > 1200) {
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
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if (this.getOwnerUUID() == null)
            buffer.writeUUID(UUID.randomUUID());
        else
            buffer.writeUUID(UUID.fromString(this.getOwnerUUID().toString()));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf data) {
        this.setOwnerId(data.readUUID());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        if (this.ownerUUID.equals(""))
            return null;
        else
            return UUID.fromString(this.ownerUUID);
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
        if (uuid != null)
            this.ownerUUID = uuid.toString();
    }
}