package dev.xkmc.lightland.content.magic.item.oriental.crafting;

import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import dev.xkmc.lightland.content.magic.item.oriental.circle.AbstractCircleMagic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

public class WandCircleRecipe extends CustomRecipe {

    public static final RecipeSerializer<WandCircleRecipe> SERIALIZER = new SimpleRecipeSerializer<>(WandCircleRecipe::new);

    public WandCircleRecipe(ResourceLocation rl) {
        super(rl);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean foundCircle = false;
        boolean foundWand = false;
        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof OrientalWand && !OrientalWand.hasCircle(stack)) {
                    if (foundWand)
                        return false;
                    else
                        foundWand = true;
                } else if (stack.getItem() instanceof AbstractCircleMagic) {
                    if (foundCircle)
                        return false;
                    else
                        foundCircle = true;
                } else {
                    return false;
                }
            }
        }
        return foundCircle && foundWand;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack circle = ItemStack.EMPTY;
        ItemStack wand = ItemStack.EMPTY;

        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof OrientalWand) {
                    wand = stack;
                } else if (stack.getItem() instanceof AbstractCircleMagic) {
                    circle = stack;
                }
            }
        }

        if (circle.isEmpty() || wand.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack wand_with_core = wand.copy();
        OrientalWand.setCircle(wand_with_core, circle);

        return wand_with_core;
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
