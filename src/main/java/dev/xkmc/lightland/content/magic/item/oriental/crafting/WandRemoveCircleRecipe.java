package dev.xkmc.lightland.content.magic.item.oriental.crafting;

import dev.xkmc.lightland.content.magic.item.oriental.OrientalWand;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class WandRemoveCircleRecipe extends CustomRecipe {

    public static final RecipeSerializer<WandRemoveCircleRecipe> SERIALIZER = new SimpleRecipeSerializer<>(WandRemoveCircleRecipe::new);

    public WandRemoveCircleRecipe(ResourceLocation rl) {
        super(rl);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean foundWand = false;

        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof OrientalWand && OrientalWand.hasCircle(stack)) {
                    if (foundWand)
                        return false;
                    else
                        foundWand = true;
                } else {
                    return false;
                }
            }
        }
        return foundWand;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack wand = ItemStack.EMPTY;
        for (int i = 0; i < inv.getMaxStackSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof OrientalWand) {
                    wand = stack;
                }
            }
        }
        ItemStack wand_empty = wand.copy();
        wand_empty.setCount(1);
        OrientalWand.setCircle(wand_empty, ItemStack.EMPTY);

        return wand_empty;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingContainer inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof OrientalWand) {
                ret.set(i, OrientalWand.getCircle(stack));
            }
        }
        return ret;
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
