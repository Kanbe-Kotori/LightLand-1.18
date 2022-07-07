package dev.xkmc.lightland.content.magic.item.oriental.circle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class InstantMagic extends AbstractCircleMagic {

    private final int standardCost;

    public InstantMagic(Properties props, int cd, int cost) {
        super(props, cd);
        this.standardCost = cost;
    }

    public static int getInstantCost(ItemStack stack) {
        if (stack.getItem() instanceof InstantMagic circle) {
            switch (getMode(stack)) {
                case EFFICIENT:
                    return (int) (circle.standardCost * 0.75F);
                case POWERFUL:
                    return (int) (circle.standardCost * 1.5F);
                case STANDARD:
                case SPEEDUP: return circle.standardCost;
                default: return 0;
            }
        } else return 0;
    }

    @Override
    public abstract InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand);

    @Override
    public void wandUseTick(Level level, LivingEntity entity, ItemStack stack, int tick) {
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick) {
    }

}
