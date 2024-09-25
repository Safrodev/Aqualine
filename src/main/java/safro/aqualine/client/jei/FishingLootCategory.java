package safro.aqualine.client.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import safro.aqualine.api.result.FishResult;
import safro.aqualine.api.result.ItemFishResult;
import safro.aqualine.registry.ItemRegistry;

public class FishingLootCategory implements IRecipeCategory<FishResult> {
    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;

    public FishingLootCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(120, 18);
        slot = guiHelper.getSlotDrawable();
        icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.STEEL_ROD));
    }

    @Override
    public RecipeType<FishResult> getRecipeType() {
        return AqualineJEIPlugin.LOOT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("text.aqualine.jei.loot");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FishResult recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 1, 1)
                .addItemStack(recipe.getDisplayStack());
    }

    @Override
    public void draw(FishResult recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        slot.draw(guiGraphics);

        String rarity = recipe.getRarity().getTranslatedName();
        String text = Component.translatable("text.aqualine.jei.rarity", rarity).getString();
        boolean range = recipe instanceof ItemFishResult result && result.range.getMinValue() != result.range.getMaxValue();
        guiGraphics.drawString(minecraft.font, text, 24, range ? 0 : 5, 0x292929, false);

        if (recipe instanceof ItemFishResult itemFishResult && range) {
            String count = Component.translatable("text.aqualine.jei.count", itemFishResult.range.getMinValue(), itemFishResult.range.getMaxValue()).getString();
            guiGraphics.drawString(minecraft.font, count, 24, 12, 0x292929, false);
        }
    }
}
