package dev.xkmc.lightland.content.magic.item.oriental.circle;

import dev.xkmc.lightland.content.common.entity.immaterial.EntityRoughFireball;
import dev.xkmc.lightland.content.magic.common.OrientalElement;
import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 技能描述：将魔力灌入火之符文中形成火球，然后靠火球自己散发的热气进行推动的简单的法术。
 * 作为法术入门的第一课，这是所有法术中几乎最基础的，同时魔力效率也拉胯到不行。
 * 值得注意的是，由于空气受热膨胀，发射的法球轨迹竟然会稍微向上偏转。
 */
public class RoughFireball extends DelayedMagic {

    public RoughFireball(Properties props) {
        super(props, 100, 20, 1);
        this.addElement(OrientalElement.FIRE, 1F);
    }

    @Override
    public InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 xAxis = player.getLookAngle().normalize();
        Vec3 shootPos = player.position()
                .add(0, player.getEyeHeight(), 0)
                .add(xAxis.scale(2));
        float modifier = OrientalWand.calcMagicModifier(stack, this);

        EntityRoughFireball ball = EntityRoughFireball.create(level, player, modifier);
        ball.setMode(AbstractCircleMagic.getMode(stack));
        ball.setPos(shootPos);
        ball.shoot(xAxis.x,xAxis.y,xAxis.z,0,0);
        level.addFreshEntity(ball);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick) {

    }
}
