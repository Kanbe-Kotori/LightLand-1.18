package dev.xkmc.lightland.content.magic.client.render.immaterial;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.xkmc.lightland.content.common.entity.immaterial.EntityCircle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderCircle extends EntityRenderer<EntityCircle> {

    public RenderCircle(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCircle p_114482_) {
        return null;
    }

    @Override
    public void render(EntityCircle entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int p_114490_) {
        super.render(entity, entityYaw, partialTicks, stack, buffer, p_114490_);

        if (entity == null)
            return;

        stack.pushPose();
        stack.translate(0, entity.getOwner().getEyeHeight(), 0);
        stack.mulPose(Vector3f.YP.rotationDegrees(entity.getXRot() - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(entity.getYRot()));

        Matrix4f m = stack.last().pose();

        float d = 2F;
        float r1 = 2.2F;
        float r2 = 1.8F;
        float w1 = 0.04F;
        float w2 = 0.02F;

        if (getScale(entity) != 1) {
            float scale = getScale(entity);
            d *= scale;
            r1 *= scale;
            r2 *= scale;
            w1 *= scale;
            w2 *= scale;
        }

        RenderHelper.round2d(buffer, m, d, 0, 0, r1, w1, 120, 0x3FFFFFFF);
        RenderHelper.round2d(buffer, m, d, 0, 0, r2, w1, 120, 0x3FFFFFFF);

        float tickRot = (entity.tickCount % 200) / 200F * 360.0F;
        stack.mulPose(Vector3f.XP.rotationDegrees(tickRot));

        float r12 = (r1 + r2) / 2;
        float rs = (r1 - r2) / 2;
        float[] sin2pi_7 = new float[7];
        float[] cos2pi_7 = new float[7];
        for (int i = 0; i < 7; i++) {
            sin2pi_7[i] = Mth.sin(i*2*Mth.PI/7);
            cos2pi_7[i] = Mth.cos(i*2*Mth.PI/7);
        }

        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[0], r12 * cos2pi_7[0], rs, w2, 60, 0x7FFF0000);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[1], r12 * cos2pi_7[1], rs, w2, 60, 0x7FFF7F00);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[2], r12 * cos2pi_7[2], rs, w2, 60, 0x7FFFFF00);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[3], r12 * cos2pi_7[3], rs, w2, 60, 0x7F00FF00);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[4], r12 * cos2pi_7[4], rs, w2, 60, 0x7F00FFFF);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[5], r12 * cos2pi_7[5], rs, w2, 60, 0x7F0000FF);
        RenderHelper.round2d(buffer, m, d, r12 * sin2pi_7[6], r12 * cos2pi_7[6], rs, w2, 60, 0x7F7F00FF);

        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[0], r12 * cos2pi_7[0],r12 * sin2pi_7[3], r12 * cos2pi_7[3],w1,0x7FFF0000,0x7F00FF00);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[3], r12 * cos2pi_7[3],r12 * sin2pi_7[6], r12 * cos2pi_7[6],w1,0x7F00FF00,0x7F7F00FF);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[6], r12 * cos2pi_7[6],r12 * sin2pi_7[2], r12 * cos2pi_7[2],w1,0x7F7F00FF,0x7FFFFF00);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[2], r12 * cos2pi_7[2],r12 * sin2pi_7[5], r12 * cos2pi_7[5],w1,0x7FFFFF00,0x7F0000FF);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[5], r12 * cos2pi_7[5],r12 * sin2pi_7[1], r12 * cos2pi_7[1],w1,0x7F0000FF,0x7FFF7F00);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[1], r12 * cos2pi_7[1],r12 * sin2pi_7[4], r12 * cos2pi_7[4],w1,0x7FFF7F00,0x7F00FFFF);
        RenderHelper.line2d(buffer,m,d,r12 * sin2pi_7[4], r12 * cos2pi_7[4],r12 * sin2pi_7[0], r12 * cos2pi_7[0],w1,0x7F00FFFF,0x7FFF0000);

        stack.mulPose(Vector3f.XP.rotationDegrees(-5*tickRot));

        float ri = r12 * Mth.cos(3 * Mth.PI / 7);

        RenderHelper.round2dRainbow(buffer, m, d, 0, 0, ri, w1, 120);

        stack.popPose();
    }

    private static float getScale(EntityCircle entity) {
        if (entity.getRemainLife() < entity.getWithdrawTime() && entity.getRemainLife() >= 0) {
            return 1F * entity.getRemainLife() / entity.getWithdrawTime();
        }
        if (entity.tickCount < entity.getDeployTime()) {
            return 1F * entity.tickCount / entity.getDeployTime();
        }
        return 1F;
    }


}