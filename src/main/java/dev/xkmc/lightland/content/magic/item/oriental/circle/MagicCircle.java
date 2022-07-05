package dev.xkmc.lightland.content.magic.item.oriental.circle;

import dev.xkmc.lightland.content.magic.common.OrientalElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public abstract class MagicCircle extends Item {

    public enum CircleMode {
        STANDARD,
        SPEEDUP,
        POWERFUL,
        EFFICIENT
    }

    protected final int standardCD;

    public HashMap<OrientalElement, Float> elements = new HashMap();

    private static final String TAG_CD = "cd";
    private static final String TAG_MODE = "mode";

    public MagicCircle(Properties props, int cd) {
        super(props.stacksTo(1));
        this.standardCD = cd;
    }

    public static int getCD(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TAG_CD);
    }

    public static void setCD(ItemStack stack, int cd) {
        stack.getOrCreateTag().putInt(TAG_CD, cd);
    }

    public static void setFullCD(ItemStack stack) {
        setCD(stack, getMaxCD(stack));
    }

    public static void tickCD(ItemStack stack) {
        if(getCD(stack) >= 1)
            setCD(stack, getCD(stack) - 1);
    }

    public static int getMaxCD(ItemStack stack) {
        if (stack.getItem() instanceof MagicCircle circle) {
            switch (getMode(stack)) {
                case SPEEDUP: return (int) (circle.standardCD * 0.75F);
                case EFFICIENT: return (int) (circle.standardCD * 1.5F);
                case STANDARD:
                case POWERFUL: return circle.standardCD;
                default: return 0;
            }
        } else return 0;
    }

    public static CircleMode getMode(ItemStack stack) {
        if (stack.getOrCreateTag().contains(TAG_MODE)) {
            return CircleMode.valueOf(stack.getOrCreateTag().getString(TAG_MODE));
        } else {
            setMode(stack, CircleMode.STANDARD);
            return CircleMode.STANDARD;
        }
    }

    public static void setMode(ItemStack stack, CircleMode mode) {
        stack.getOrCreateTag().putString(TAG_MODE, mode.name());
    }

    public MagicCircle addElement(OrientalElement element, float scale) {
        elements.put(element, scale);
        return this;
    }

    public float calcTotalBuff(HashMap<OrientalElement, Float> buffs) {
        float total = (float) elements.keySet().stream().mapToDouble(e->elements.get(e)*buffs.getOrDefault(e,0F)).sum();
        return Math.max(total + 1, 0);
    }

    public static float getDamageModifier(ItemStack stack) {
        switch (getMode(stack)) {
            case SPEEDUP: return 0.75F;
            case POWERFUL: return 1.5F;
            case STANDARD:
            case EFFICIENT: return 1F;
            default: return 0F;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (getCD(stack) > 0) {
            setCD(stack, getCD(stack) - 1);
        }
    }

    public abstract InteractionResultHolder<ItemStack> wandUse(Level level, Player player, InteractionHand hand);

    public abstract void wandUseTick(Level level, LivingEntity entity, ItemStack stack, int tick);

    public abstract void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int tick);

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCD(stack) > 0 && getMaxCD(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (getMaxCD(stack) > 0)
            return Math.round(13F - 13F * getCD(stack) / getMaxCD(stack));
        else
            return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (getMaxCD(stack) > 0) {
            float f = Math.max(0.0F, 1.0F * (getMaxCD(stack) - getCD(stack)) / getMaxCD(stack));
            return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
        } else return Mth.hsvToRgb(1.0F / 3, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flags) {
        if (getMaxCD(stack) == 0) {
            Component no_cd = new TranslatableComponent("lightland.misc.circle_no_cd");
            list.add(no_cd);
            return;
        }
        Component total_cd = new TranslatableComponent("lightland.misc.total_cd", getMaxCD(stack));
        Component cd_info;
        if (getCD(stack) > 0)
            cd_info = new TranslatableComponent("lightland.misc.cd_not_complete", getCD(stack)).withStyle(ChatFormatting.RED);
        else
            cd_info = new TranslatableComponent("lightland.misc.cd_complete").withStyle(ChatFormatting.GREEN);
        list.add(total_cd);
        list.add(cd_info);
    }

}
