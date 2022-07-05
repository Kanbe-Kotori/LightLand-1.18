package dev.xkmc.lightland.content.magic.item.oriental;

import dev.xkmc.lightland.content.common.entity.immaterial.EntityCircle;
import dev.xkmc.lightland.content.magic.common.OrientalElement;
import dev.xkmc.lightland.content.magic.item.oriental.circle.ContinuousMagic;
import dev.xkmc.lightland.content.magic.item.oriental.circle.DelayedMagic;
import dev.xkmc.lightland.content.magic.item.oriental.circle.InstantMagic;
import dev.xkmc.lightland.content.magic.item.oriental.circle.MagicCircle;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class OrientalWand extends Item {

    public HashMap<OrientalElement, Float> elemental_buffs = new HashMap();

    private static final String TAG_CIRCLE = "circle";

    public OrientalWand(Properties props) {
        super(props);
    }

    public OrientalWand addBuff(OrientalElement element, float buff) {
        elemental_buffs.put(element, buff);
        return this;
    }

    public static void setCircle(ItemStack stack, ItemStack core) {
        CompoundTag tag = new CompoundTag();
        core.save(tag);
        stack.getOrCreateTag().put(TAG_CIRCLE, tag);
    }

    public static ItemStack getCircle(ItemStack stack) {
        if (stack.getOrCreateTag().contains(TAG_CIRCLE)) {
            return ItemStack.of(stack.getOrCreateTag().getCompound(TAG_CIRCLE));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static boolean hasCircle(ItemStack stack) {
        return !getCircle(stack).isEmpty();
    }

    public static int getCircleCD(ItemStack stack) {
        ItemStack circle = getCircle(stack);
        if (!circle.isEmpty())
            return MagicCircle.getCD(circle);
        else
            return 0;
    }

    public static void setCircleFullCD(ItemStack stack) {
        ItemStack circle = getCircle(stack);
        if (!circle.isEmpty())
            MagicCircle.setFullCD(circle);
    }

    public static void tickCircle(ItemStack stack) {
        ItemStack circle = getCircle(stack);
        if (!circle.isEmpty())
            MagicCircle.tickCD(circle);
    }

    public static int getMaxCircleCD(ItemStack stack) {
        ItemStack circle = getCircle(stack);
        if (!circle.isEmpty())
            return MagicCircle.getMaxCD(circle);
        else
            return 0;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (getCircleCD(stack) > 0) {
            tickCircle(stack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCircleCD(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (getMaxCircleCD(stack) == 0)
            return 0;
        return Math.round(13F - 13F * getCircleCD(stack) / getMaxCircleCD(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (getMaxCircleCD(stack) == 0)
            return 0;
        float f = Math.max(0.0F, 1.0F * (getMaxCircleCD(stack) - getCircleCD(stack)) / getMaxCircleCD(stack));
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (hasCircle(stack)) {
            MutableComponent component = super.getName(stack).plainCopy();
            component.append("(");
            component.append(getCircle(stack).getDisplayName().plainCopy().withStyle(ChatFormatting.GREEN));
            component.append(")");
            return component;
        } else {
            return super.getName(stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int tick) {
        if (level.isClientSide)
            return;
        if (getCircle(stack) != null && getCircle(stack).getItem() instanceof ContinuousMagic con) {
            con.wandUseTick(level, entity, stack, tick);
        } else if (getCircle(stack) != null && getCircle(stack).getItem() instanceof DelayedMagic delay) {
            delay.wandUseTick(level, entity, stack, tick);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick) {
        if (level.isClientSide)
            return;
        if (getCircle(stack) != null && getCircle(stack).getItem() instanceof DelayedMagic delay) {
            delay.releaseUsing(stack, level, entity, tick);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);

        if (!hasCircle(stack))
            return InteractionResultHolder.pass(stack);
        if (getCircleCD(stack) > 0 && !player.isCreative())
            return InteractionResultHolder.pass(stack);

        ItemStack circleStack = getCircle(stack);
        if (circleStack.getItem() instanceof InstantMagic instant) {
            InteractionResultHolder<ItemStack> result = instant.wandUse(level, player, hand);
            if (result.getResult() != InteractionResult.PASS) {
                EntityCircle circle = EntityCircle.create(level, player, 0, 5);
                level.addFreshEntity(circle);
                setCircleFullCD(stack);
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.pass(stack);
        } else if (circleStack.getItem() instanceof ContinuousMagic con) {
            InteractionResultHolder<ItemStack> result = con.wandUse(level, player, hand);
            if (result.getResult() != InteractionResult.PASS) {
                EntityCircle circle = EntityCircle.create(level, player);
                level.addFreshEntity(circle);
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.pass(stack);
        } else if (circleStack.getItem() instanceof DelayedMagic delay) {
            InteractionResultHolder<ItemStack> result = delay.wandUse(level, player, hand);
            if (result.getResult() != InteractionResult.PASS) {
                EntityCircle circle = EntityCircle.create(level, player);
                level.addFreshEntity(circle);
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.pass(stack);
        }
        else return InteractionResultHolder.pass(stack);
    }

}
