package dev.xkmc.cuisine.compat.jei;

import dev.hikarishima.lightland.compat.jei.screen.ExtraInfoScreen;
import dev.hikarishima.lightland.init.LightLand;
import dev.xkmc.cuisine.compat.jei.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CuisineJeiPlugin implements IModPlugin {

	public static CuisineJeiPlugin INSTANCE;

	public final ResourceLocation UID = new ResourceLocation(LightLand.MODID, "cuisine");

	public final List<BaseCuisineRecipeCategory<?, ?>> LIST = List.of(
			new PanRecipeCategory(),
			new JarRecipeCategory(),
			new BasinRecipeCategory(),
			new BasinDryRecipeCategory(),
			new MillRecipeCategory(),
			new MortarRecipeCategory(),
			new FirePitStickRecipeCategory());

	public IGuiHelper GUI_HELPER;

	public CuisineJeiPlugin() {
		INSTANCE = this;
	}

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
		LIST.forEach(e -> registration.addRecipeCategories(e.init(helper)));
		GUI_HELPER = helper;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		LIST.forEach(e -> e.registerRecipes(registration));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		LIST.forEach(e -> e.registerBlocks(registration));
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		ExtraInfoScreen.init();
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

}
