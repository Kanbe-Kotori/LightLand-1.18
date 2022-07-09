package dev.xkmc.lightland.content.magic.item.oriental.circle;

import dev.xkmc.lightland.content.common.entity.immaterial.EntityRainbowOrb;
import dev.xkmc.lightland.content.magic.common.OrientalElement;
import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class RainbowBarrage extends ContinuousMagic {

    public static final int STANDARD_INTERVAL = 8;

    public RainbowBarrage(Properties props) {
        super(props, 0, 1, 1);
        this.addElement(OrientalElement.WOOD, 0.25F);
        this.addElement(OrientalElement.FIRE, 0.25F);
        this.addElement(OrientalElement.SUN, 0.25F);
        this.addElement(OrientalElement.MOON, 0.25F);
        this.addElement(OrientalElement.GOLD, 0.25F);
        this.addElement(OrientalElement.WATER, 0.25F);
        this.addElement(OrientalElement.EARTH, 0.25F);
    }

    @Override
    public void wandUseTick(Level level, Player player, ItemStack stack, int tick) {
        int time = 72000 - tick;
        if (time % getInterval(OrientalWand.getCircle(stack), STANDARD_INTERVAL) == 0) {
            Random random = new Random();
            Vec3 xAxis = player.getLookAngle().normalize();
            Vec3 yAxis = new Vec3(-xAxis.y, xAxis.x, 0).normalize();
            Vec3 zAxis = xAxis.cross(yAxis).normalize();
            float r = random.nextFloat() * 2 * Mth.cos(3 * Mth.PI / 7);
            float theta = random.nextFloat() * 2 * Mth.PI;
            float v = 2F + 1 * random.nextFloat();
            float modifier = OrientalWand.calcMagicModifier(stack, this);

            for (int i = 0; i < 7; i++) {
                theta += 2 * Mth.PI / 7;
                Vec3 shootPos = player.position()
                        .add(0, player.getEyeHeight(), 0)
                        .add(xAxis.scale(2))
                        .add(yAxis.scale(r * Mth.sin(theta)))
                        .add(zAxis.scale(r * Mth.cos(theta)));
                for (int j = 0; j < 5; j++) {
                    float phi = Mth.PI * (5 + 10 * j) / 180;
                    Vec3 shootVec = xAxis.add(yAxis.scale(Mth.sin(phi) * Mth.sin(theta))).add(zAxis.scale(Mth.sin(phi) * Mth.cos(theta)));
                    EntityRainbowOrb orb = EntityRainbowOrb.create(level, player, modifier);
                    orb.setPos(shootPos);
                    orb.shoot(shootVec.x, shootVec.y, shootVec.z, v, 2);
                    level.addFreshEntity(orb);
                }
            }
        }
    }

    @Override
    public void wandReleaseUsing(ItemStack stack, Level level, Player player, int tick) {

    }
}
