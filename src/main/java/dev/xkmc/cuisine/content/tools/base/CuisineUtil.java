package dev.xkmc.cuisine.content.tools.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Random;

public class CuisineUtil {

	@Nonnull
	public static Ingredient getContainer(ItemStack result) {
		ItemStack container = result.getItem().getContainerItem(result);
		if (!container.isEmpty())
			return Ingredient.of(container);
		if (result.getItem() instanceof BowlFoodItem) {
			return Ingredient.of(Items.BOWL);
		}
		return Ingredient.EMPTY;
	}

	public static void spawnParticle(Level world, BlockPos pos, Random r) {
		double d0 = pos.getX() + 1 - r.nextFloat() * 0.5F;
		double d1 = pos.getY() + 1 - r.nextFloat() * 0.5F;
		double d2 = pos.getZ() + 1 - r.nextFloat() * 0.5F;
		if (r.nextInt(5) == 0) {
			world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, r.nextGaussian() * 0.005D, r.nextGaussian() * 0.005D, r.nextGaussian() * 0.005D);
		}
	}

}