package dev.xkmc.lightland.content.magic.item.oriental.circle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ContinuousMagic extends AbstractCircleMagic {

    private final int standardStartupCost;
    private final int standardConCost;

    public ContinuousMagic(Properties props, int cd, int startupCost, int conCost) {
        super(props, cd);
        this.standardStartupCost = startupCost;
        this.standardConCost = conCost;
    }

    public static int getStartupCost(ItemStack stack) {
        if (stack.getItem() instanceof ContinuousMagic circle) {
            switch (getMode(stack)) {
                case EFFICIENT: return (int) (circle.standardStartupCost * 0.75F);
                case POWERFUL: return (int) (circle.standardStartupCost * 1.5F);
                case STANDARD:
                case SPEEDUP: return circle.standardStartupCost;
                default: return 0;
            }
        } else return 0;
    }

    public static int getConCost(ItemStack stack) {
        if (stack.getItem() instanceof ContinuousMagic circle) {
            switch (getMode(stack)) {
                case EFFICIENT: return (int) (circle.standardConCost * 0.75F);
                case POWERFUL: return (int) (circle.standardConCost * 1.5F);
                case STANDARD:
                case SPEEDUP: return circle.standardConCost;
                default: return 0;
            }
        } else return 0;
    }

    public static int getInterval(ItemStack stack, int tick) {
        if (stack.getItem() instanceof ContinuousMagic) {
            switch (getMode(stack)) {
                case SPEEDUP: return (int) (tick * 0.75F);
                case EFFICIENT: return (int) (tick * 1.5F);
                case STANDARD:
                case POWERFUL: return tick;
                default: return Integer.MAX_VALUE;
            }
        } else return Integer.MAX_VALUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public abstract void wandUseTick(Level level, Player player, ItemStack stack, int tick);

    @Override
    public abstract void wandReleaseUsing(ItemStack stack, Level level, Player player, int tick);
}
