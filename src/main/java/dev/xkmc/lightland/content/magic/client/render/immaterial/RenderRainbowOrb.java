package dev.xkmc.lightland.content.magic.client.render.immaterial;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.xkmc.lightland.content.common.entity.immaterial.EntityRainbowOrb;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderRainbowOrb extends EntityRenderer<EntityRainbowOrb> {

    public RenderRainbowOrb(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRainbowOrb p_114482_) {
        return null;
    }

    @Override
    public void render(EntityRainbowOrb entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int p_114490_) {
        super.render(entity, entityYaw, partialTicks, stack, buffer, p_114490_);

        stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(entity.getXRot() - 90.0F));
        stack.mulPose(Vector3f.ZP.rotationDegrees(entity.getYRot()));

        Matrix4f m = stack.last().pose();

        RenderHelper.sphere(buffer, m, 0, 0, 0, 0.2F, 30, getColor(entity));

        stack.popPose();
    }

    private int getColor(EntityRainbowOrb orb) {
        switch (orb.getOrbType()) {
            case 0: return 0x7FFF0000;
            case 1: return 0x7FFF7F00;
            case 2: return 0x7FFFFF00;
            case 3: return 0x7F00FF00;
            case 4: return 0x7F00FFFF;
            case 5: return 0x7F0000FF;
            case 6: return 0x7F7F00FF;
            default: return 0x00000000;
        }
    }

}
