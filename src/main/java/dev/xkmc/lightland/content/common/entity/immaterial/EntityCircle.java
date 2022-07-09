package dev.xkmc.lightland.content.common.entity.immaterial;

import dev.xkmc.lightland.content.magic.client.render.immaterial.CircleRune;
import dev.xkmc.lightland.init.registrate.LightlandEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class EntityCircle extends EntityHasOwner implements IModedEntity {

    protected int deployTime = 10;
    protected int withdrawTime = 10;

    protected ArrayList<CircleRune> runes = new ArrayList();

    public EntityCircle(EntityType<?> type, Level level) {
        super(type, level);
    }

    @OnlyIn(Dist.CLIENT)
    public ArrayList<CircleRune> getRunes() {
        if (runes.size() < 35) {
            for (int i = 0; i < 35 - runes.size(); i++)
                runes.add(CircleRune.create());
        }
        return runes;
    }

    public static EntityCircle create(Level level, Player player, int deploy, int withdraw, int life) {
        EntityCircle circle = new EntityCircle(LightlandEntities.CIRCLE.get(), level);
        circle.setOwner(player);
        circle.deployTime = deploy;
        circle.withdrawTime = withdraw;
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
        return create(level, player, 10, 10, -1);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.deployTime);
        buffer.writeInt(this.withdrawTime);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf data) {
        super.readSpawnData(data);
        this.deployTime = data.readInt();
        this.withdrawTime = data.readInt();
    }

    public int getDeployTime() {
        return this.deployTime;
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

    @Override
    public void powerful() {

    }

    @Override
    public void efficient() {
        this.deployTime *= 1.5F;
        this.withdrawTime *= 1.5F;
    }

    @Override
    public void speedup() {
        this.deployTime *= 0.75F;
        this.withdrawTime *= 0.75F;
    }
}
