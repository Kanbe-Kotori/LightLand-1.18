package dev.lcy0x1.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CustomShapedBuilder<T extends AbstractShapedRecipe<T>> extends ShapedRecipeBuilder {

	private final RegistryEntry<AbstractShapedRecipe.Serializer<T>> serializer;

	public CustomShapedBuilder(RegistryEntry<AbstractShapedRecipe.Serializer<T>> serializer, ItemLike result, int count) {
		super(result, count);
		this.serializer = serializer;
	}

	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		CreativeModeTab group = this.result.getItemCategory();
		String folder = (group == null ? "nil" : group.getRecipeFolderName()) + "/";
		pvd.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group,
				this.rows, this.key, this.advancement, new ResourceLocation(id.getNamespace(),
				"recipes/" + folder + id.getPath())));
	}


	public CustomShapedBuilder<T> unlockedBy(RegistrateRecipeProvider pvd, ItemLike item) {
		this.advancement.addCriterion("has_" + pvd.safeName(item.asItem()),
				DataIngredient.items(item.asItem()).getCritereon(pvd));
		return this;
	}

	class Result extends ShapedRecipeBuilder.Result {

		public Result(ResourceLocation id, Item result, int count, String group, List<String> pattern,
					  Map<Character, Ingredient> key, Advancement.Builder advancement, ResourceLocation path) {
			super(id, result, count, group, pattern, key, advancement, path);
		}

		@Override
		public RecipeSerializer<?> getType() {
			return serializer.get();
		}
	}


}
