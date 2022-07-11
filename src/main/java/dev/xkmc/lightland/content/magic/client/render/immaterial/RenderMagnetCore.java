package dev.xkmc.lightland.content.magic.client.render.immaterial;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.xkmc.lightland.content.common.entity.immaterial.EntityMagnetCore;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderMagnetCore extends EntityRenderer<EntityMagnetCore> {
    public RenderMagnetCore(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMagnetCore p_114482_) {
        return null;
    }

    @Override
    public void render(EntityMagnetCore entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int p_114490_) {
        super.render(entity, entityYaw, partialTicks, stack, buffer, p_114490_);

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(entity.getXRot() - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(entity.getYRot()));

        Matrix4f m = stack.last().pose();

        RenderHelper.sphere(buffer, m, 0, 0, 0, getR1(entity.tickCount), 30, getColor1(entity.tickCount));
        RenderHelper.sphere(buffer, m, 0, 0, 0, getR2(entity.tickCount), 30, getColor2(entity.tickCount));

        stack.popPose();
    }

    private static float getR1(int tick) {
        if (tick < EntityMagnetCore.explode_time) {
            return 0.2F + 0.8F * tick / EntityMagnetCore.explode_time;
        } else {
            tick -= EntityMagnetCore.explode_time;
            return 1 + 10 * tick / (EntityMagnetCore.life - EntityMagnetCore.explode_time);
        }
    }

    private static int getColor1(int tick) {
        if (tick < EntityMagnetCore.explode_time) {
            int a = (int) (0xFF * (0.1F + 0.4F * tick / EntityMagnetCore.explode_time));
            return a << 24 | 0xDFCF00;
        } else {
            tick -= EntityMagnetCore.explode_time;
            int a = (int) (0xFF * (0.5F - 0.5F * tick / (EntityMagnetCore.life - EntityMagnetCore.explode_time)));
            return a << 24 | 0xDFCF00;
        }
    }

    private static float getR2(int tick) {
        float p = 0;
        if (tick < 24) {
            p = tick/24F;
        } else if (tick < 44) {
            p = (tick-24)/20F;
        } else if (tick < 60) {
            p = (tick-44)/16F;
        } else if (tick < 72) {
            p = (tick-60)/12F;
        } else if (tick < 80) {
            p = (tick-72)/8F;
        } else {
            return 0;
        }
        return 8 - 8 * p;
    }

    private static int getColor2(int tick) {
        float p = 0;
        if (tick < 24) {
            p = tick/24F;
        } else if (tick < 44) {
            p = (tick-24)/20F;
        } else if (tick < 60) {
            p = (tick-44)/16F;
        } else if (tick < 72) {
            p = (tick-60)/12F;
        } else if (tick < 80) {
            p = (tick-72)/8F;
        } else {
            return 0;
        }
        int a = (int) (0xFF * (0.1F + 0.1F * p));
        return a << 24 | 0xDFCF00;
    }

}
