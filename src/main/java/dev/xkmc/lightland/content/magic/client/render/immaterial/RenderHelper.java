package dev.xkmc.lightland.content.magic.client.render.immaterial;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.util.OptionalDouble;

public class RenderHelper {

    public static void sphere(MultiBufferSource buffer, Matrix4f m, float x, float y, float z, float r, int gradation, int color) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.triangleStrip());
        for (int i = 0; i < gradation; i++) {
            float alpha = (float) (i * Math.PI / gradation);
            for (int j = 0; j <= gradation; j++) {
                float beta = j * 2 * Mth.PI / gradation;
                float x1 = x + r * Mth.cos(beta) * Mth.sin(alpha);
                float y1 = y + r * Mth.sin(beta) * Mth.sin(alpha);
                float z1 = z + r * Mth.cos(alpha);
                vc.vertex(m, x1, y1, z1).color(color).endVertex();
                x1 = x + r * Mth.cos(beta) * Mth.sin(alpha + Mth.PI / gradation);
                y1 = y + r * Mth.sin(beta) * Mth.sin(alpha + Mth.PI / gradation);
                z1 = z + r * Mth.cos(alpha + Mth.PI / gradation);
                vc.vertex(m, x1, y1, z1).color(color).endVertex();
            }
        }
    }

    public static void cone2d(MultiBufferSource buffer, Matrix4f m, float x1, float x2, float y, float z, float r, int gradation, int color1, int color2) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.triangleFan());
        vc.vertex(m, x2, y, z).color(color2).endVertex();
        for (int i = 0; i <= gradation; i++) {
            float theta = i * 2 * Mth.PI / gradation;
            float yOffset = r * Mth.sin(theta);
            float zOffset = r * Mth.cos(theta);
            vc.vertex(m, x1, y + yOffset, z + zOffset).color(color1).endVertex();
        }
    }

    public static void ring2d(MultiBufferSource buffer, Matrix4f m, float x, float y, float z, float r, float w, int gradation, int color) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.triangleStrip());
        for (int i = 0; i <= gradation; ++i) {
            float yPos = y + r * Mth.sin(i * 2 * Mth.PI / gradation);
            float zPos = z + r * Mth.cos(i * 2 * Mth.PI / gradation);
            float yOffset = w / 2 * Mth.sin(i * 2 * Mth.PI / gradation);
            float zOffset = w / 2 * Mth.cos(i * 2 * Mth.PI / gradation);
            vc.vertex(m, x, yPos - yOffset, zPos - zOffset).color(color).endVertex();
            vc.vertex(m, x, yPos + yOffset, zPos + zOffset).color(color).endVertex();
        }
    }

    public static void ring2dRainbow(MultiBufferSource buffer, Matrix4f m, float x, float y, float z, float r, float w, int gradation) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.triangleStrip());
        for (int i = 0; i <= gradation; ++i) {
            int color = getRainbowColor((double)i / gradation);
            float yPos = y + r * Mth.sin(i * 2 * Mth.PI / gradation);
            float zPos = z + r * Mth.cos(i * 2 * Mth.PI / gradation);
            float yOffset = w / 2 * Mth.sin(i * 2 * Mth.PI / gradation);
            float zOffset = w / 2 * Mth.cos(i * 2 * Mth.PI / gradation);
            vc.vertex(m, x, yPos - yOffset, zPos - zOffset).color(color).endVertex();
            vc.vertex(m, x, yPos + yOffset, zPos + zOffset).color(color).endVertex();
        }
    }

    public static void line2d(MultiBufferSource buffer, Matrix4f m, float x, float y1, float z1, float y2, float z2, float width, int color1, int color2) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.quads());
        float dy = y1 - y2;
        float dz = z1 - z2;
        float yz = Mth.sqrt(dy * dy + dz * dz);

        float wdy = width * dz / yz / 2;
        float wdz = -width * dy / yz / 2;

        vc.vertex(m, x, y1 - wdy, z1 - wdz).color(color1).endVertex();
        vc.vertex(m, x, y2 - wdy, z2 - wdz).color(color2).endVertex();
        vc.vertex(m, x, y2 + wdy, z2 + wdz).color(color2).endVertex();
        vc.vertex(m, x, y1 + wdy, z1 + wdz).color(color1).endVertex();
    }

    public static void line2d(MultiBufferSource buffer, Matrix4f m, float x, float y1, float z1, float y2, float z2, float width, int color) {
        line2d(buffer, m, x, y1, z1, y2, z2, width, color, color);
    }

        public static void rune2d(MultiBufferSource buffer, Matrix4f m, CircleRune rune, float x, float y, float z, float size, int color) {
        VertexConsumer vc = buffer.getBuffer(ImmaterialRenderType.quads());
        int[][] runeColor = rune.getBitmap(color);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                float y1 = y + (i - 3) * size;
                float z1 = z + (j - 4) * size;
                vc.vertex(m, x, y1, z1).color(runeColor[i][j]).endVertex();
                vc.vertex(m, x, y1, z1 + size).color(runeColor[i][j]).endVertex();
                vc.vertex(m, x, y1 + size, z1 + size).color(runeColor[i][j]).endVertex();
                vc.vertex(m, x, y1 + size, z1).color(runeColor[i][j]).endVertex();
            }
        }
    }

    public static int getRainbowColor(double angle) {
        int a = 0x7F;
        int r = 0, g = 0, b = 0;
        double p = (angle * 6) % 1;
        double mp = 1 - p;
        if (angle < 1D/6) {
            r = 0xFF;
            g = (int) (0xFF * p);
        } else if (angle < 2D/6) {
            r = (int) (0xFF * mp);
            g = 0xFF;
        } else if (angle < 3D/6) {
            g = 0xFF;
            b = (int) (0xFF * p);
        } else if (angle < 4D/6) {
            g = (int) (0xFF * mp);
            b = 0xFF;
        } else if (angle < 5D/6) {
            r = (int) (0xFF * p);
            b = 0xFF;
        } else if (angle < 1D) {
            r = 0xFF;
            b = (int) (0xFF * mp);
        } else {
            r = 0xFF;
        }
        return a << 24 | r << 16 | g << 8 | b;
    }
}

class ImmaterialRenderType extends RenderType {

    public ImmaterialRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType triangleStrip() {
        return RenderType.create(
                "triangle_strip",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.TRIANGLE_STRIP,
                1024, true, true,
                RenderType.CompositeState.builder()
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        //.setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        );
    }

    public static RenderType triangleFan() {
        return RenderType.create(
                "triangle_fan",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.TRIANGLE_FAN,
                1024, true, true,
                RenderType.CompositeState.builder()
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        //.setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        );
    }

    public static RenderType quads() {
        return RenderType.create(
                "quads",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256, true, true,
                RenderType.CompositeState.builder()
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(POSITION_COLOR_SHADER)
                        //.setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        );
    }

    public static RenderType lineStrip(double width) {
        return RenderType.create(
                "line_strip",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.LINE_STRIP,
                1024, true, true,
                RenderType.CompositeState.builder()
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(width)))
                        .setTextureState(NO_TEXTURE)
                        .setShaderState(RENDERTYPE_LINES_SHADER)
                        //.setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(false)
        );
    }

}
