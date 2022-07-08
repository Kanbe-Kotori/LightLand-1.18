package dev.xkmc.lightland.content.magic.item.oriental.circle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
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

    @Override
    public InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public abstract void wandUseTick(Level level, LivingEntity entity, ItemStack stack, int tick);

    @Override
    public abstract void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick);
}
