package dev.xkmc.lightland.content.magic.item.oriental.circle;

import dev.xkmc.lightland.content.common.entity.immaterial.EntityMagnetCore;
import dev.xkmc.lightland.content.magic.common.OrientalElement;
import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagnetCore extends InstantMagic {
    public MagnetCore(Properties props) {
        super(props, 100, 1);
        this.addElement(OrientalElement.GOLD, 0.75F);
        this.addElement(OrientalElement.EARTH, 0.25F);
    }

    @Override
    public InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 xAxis = player.getLookAngle().normalize();
        Vec3 shootPos = player.position()
                .add(0, player.getEyeHeight(), 0)
                .add(xAxis.scale(2));
        Vec3 endPos = shootPos.add(xAxis.scale(6));
        HitResult hitPos = level.clip(new ClipContext(shootPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        Vec3 spawnPos = hitPos.getType() != HitResult.Type.MISS? hitPos.getLocation() : endPos;
        float modifier = OrientalWand.calcMagicModifier(stack, this);

        EntityMagnetCore core = EntityMagnetCore.create(level, player, modifier);
        core.setMode(AbstractCircleMagic.getMode(OrientalWand.getCircle(stack)));
        core.setPos(spawnPos);
        level.addFreshEntity(core);

        return InteractionResultHolder.success(stack);
    }
}
