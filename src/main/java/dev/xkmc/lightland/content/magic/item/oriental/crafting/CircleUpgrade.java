package dev.xkmc.lightland.content.magic.item.oriental.crafting;

import dev.xkmc.lightland.content.magic.item.oriental.ModeUpgrade;
import dev.xkmc.lightland.content.magic.item.oriental.circle.AbstractCircleMagic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

public class CircleUpgrade extends CustomRecipe {

    public static final RecipeSerializer<CircleUpgrade> SERIALIZER = new SimpleRecipeSerializer<>(CircleUpgrade::new);

    public CircleUpgrade(ResourceLocation rl) {
        super(rl);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean foundCircle = false;
        boolean foundUpgrade = false;
        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof AbstractCircleMagic && AbstractCircleMagic.getMode(stack) == AbstractCircleMagic.CircleMode.STANDARD) {
                    if (foundCircle)
                        return false;
                    else
                        foundCircle = true;
                } else if (stack.getItem() instanceof ModeUpgrade) {
                    if (foundUpgrade)
                        return false;
                    else
                        foundUpgrade = true;
                } else {
                    return false;
                }
            }
        }
        return foundCircle && foundUpgrade;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack circle = ItemStack.EMPTY;
        ItemStack upgrade = ItemStack.EMPTY;
        AbstractCircleMagic.CircleMode mode = AbstractCircleMagic.CircleMode.STANDARD;

        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ModeUpgrade item) {
                    upgrade = stack;
                    mode = item.upgradeType;
                } else if (stack.getItem() instanceof AbstractCircleMagic) {
                    circle = stack;
                }
            }
        }

        if (circle.isEmpty() || upgrade.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack upgradedCircle = circle.copy();
        AbstractCircleMagic.setMode(upgradedCircle, mode);

        return upgradedCircle;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
