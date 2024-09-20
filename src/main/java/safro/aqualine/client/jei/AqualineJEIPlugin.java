package safro.aqualine.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingManager;
import safro.aqualine.api.result.FishResult;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AqualineJEIPlugin implements IModPlugin {
    public static final RecipeType<FishResult> LOOT_RECIPE_TYPE = RecipeType.create(Aqualine.MODID, "fishing_loot", FishResult.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new FishingLootCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<FishResult> list = new ArrayList<>();
        list.addAll(FishingManager.LOOT);
        list.addAll(FishingManager.ENTITIES);
        registration.addRecipes(LOOT_RECIPE_TYPE, list);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return Aqualine.id("main");
    }
}
