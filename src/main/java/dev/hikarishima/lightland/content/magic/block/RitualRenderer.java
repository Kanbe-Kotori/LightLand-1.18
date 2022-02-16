package dev.hikarishima.lightland.content.magic.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RitualRenderer implements BlockEntityRenderer<RitualTE> {

    public RitualRenderer(BlockEntityRendererProvider.Context dispatcher) {

    }

    @Override
    public void render(RitualTE te, float partial, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        float time = Math.floorMod(Objects.requireNonNull(te.getLevel()).getGameTime(), 80L) + partial;
        ItemStack is = te.getItem(0);
        if (!is.isEmpty()) {
            matrix.pushPose();
            double offset = (Math.sin(time * 2 * Math.PI / 40.0) - 3) / 16;
            matrix.translate(0.5, 1.5 + offset, 0.5);
            matrix.mulPose(Vector3f.YP.rotationDegrees(time * 4.5f));
            Minecraft.getInstance().getItemRenderer().renderStatic(is, ItemTransforms.TransformType.GROUND, light,
                    overlay, matrix, buffer, 0);
            matrix.popPose();
        }
    }

}