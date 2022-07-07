package dev.xkmc.lightland.content.magic.item.oriental.circle;

import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class DelayedMagic extends AbstractCircleMagic {

    private final int standardCost;
    private final int standardDelay;

    public DelayedMagic(Properties props, int cd, int delay, int cost) {
        super(props, cd);
        this.standardCost = cost;
        this.standardDelay = delay;
    }

    public static int getDelayCost(ItemStack stack) {
        if (stack.getItem() instanceof DelayedMagic circle) {
            switch (getMode(stack)) {
                case EFFICIENT: return (int) (circle.standardCost * 0.75F);
                case POWERFUL: return (int) (circle.standardCost * 1.5F);
                case STANDARD:
                case SPEEDUP: return circle.standardCost;
                default: return 0;
            }
        } else return 0;
    }

    public static int getDelay(ItemStack stack) {
        if (stack.getItem() instanceof DelayedMagic circle) {
            switch (getMode(stack)) {
                case SPEEDUP: return (int) (circle.standardDelay * 0.75F);
                case EFFICIENT: return (int) (circle.standardDelay * 1.5F);
                case STANDARD:
                case POWERFUL: return circle.standardDelay;
                default: return 0;
            }
        } else return 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void wandUseTick(Level level, LivingEntity entity, ItemStack stack, int tick) {
        int useTick = stack.getUseDuration() - tick;
        if (useTick > getDelay(OrientalWand.getCircle(stack))) {
            entity.stopUsingItem();
            OrientalWand.setCircleFullCD(stack);
        }
    }

    @Override
    public abstract void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick);

}
