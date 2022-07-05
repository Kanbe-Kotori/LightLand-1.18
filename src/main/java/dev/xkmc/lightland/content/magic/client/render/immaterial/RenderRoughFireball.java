package dev.xkmc.lightland.content.magic.client.render.immaterial;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.xkmc.lightland.content.common.entity.immaterial.EntityRoughFireball;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderRoughFireball extends EntityRenderer<EntityRoughFireball> {

    public RenderRoughFireball(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRoughFireball p_114482_) {
        return null;
    }

    @Override
    public void render(EntityRoughFireball entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int p_114490_) {
        super.render(entity, entityYaw, partialTicks, stack, buffer, p_114490_);

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(entity.getXRot() - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(entity.getYRot()));

        Matrix4f m = stack.last().pose();

        RenderHelper.sphere(buffer, m, 0, 0, 0, getR(entity), 30, 0x7FFF0000);

        if (entity.tickCount > entity.getChargeTime())
            RenderHelper.cone2d(buffer, m, 0, getL(entity), 0, 0, getR(entity), 30, 0x7FFF0000, 0x00FF0000);

        stack.popPose();
    }

    private static float getR(EntityRoughFireball entity) {
        int tick = entity.tickCount;
        int charge = entity.getChargeTime();
        if (tick <= charge) {
            return 0.5F * tick / charge;
        } else {
            return Math.max(0, 0.5F - 0.01F * (tick - charge));
        }
    }

    private static float getL(EntityRoughFireball entity) {
        int tick = entity.tickCount;
        int charge = entity.getChargeTime();
        return -0.1F * (tick - charge);
    }
}
