package safro.aqualine.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import safro.aqualine.Aqualine;

import java.util.List;

public class FishingCharmItem extends Item {
    private static final ResourceLocation MODIFIER = ResourceLocation.fromNamespaceAndPath(Aqualine.MODID, "fishing_charm");
    private final int luck;

    public FishingCharmItem(Properties properties, int luck) {
        super(properties);
        this.luck = luck;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide()) {
            if (player.fishing != null) {
                player.getAttribute(Attributes.LUCK).addOrUpdateTransientModifier(new AttributeModifier(MODIFIER, this.luck, AttributeModifier.Operation.ADD_VALUE));
            } else {
                player.getAttribute(Attributes.LUCK).removeModifier(MODIFIER);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("text.aqualine.fish_charm", this.luck));
    }
}
