package safro.aqualine.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingAttributes;
import safro.aqualine.entity.CustomFishingHook;

import java.util.List;

public class AqualineRodItem extends FishingRodItem {
    public static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(Aqualine.MODID, "base_fishing_speed");
    public static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(Aqualine.MODID, "base_luck");
    private final int lineColor;
    @Nullable
    private final TagKey<Biome> biomeBonus;
    private final int speedBonus;
    private final String tooltipKey;

    public AqualineRodItem(Properties properties) {
        this(properties, null, -1, "", FastColor.ARGB32.color(0, 0, 0));
    }

    public AqualineRodItem(Properties properties, @Nullable TagKey<Biome> biomeBonus, int speedBonus, String tooltip, int color) {
        super(properties);
        this.biomeBonus = biomeBonus;
        this.speedBonus = speedBonus;
        this.tooltipKey = tooltip;
        this.lineColor = color;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.fishing instanceof CustomFishingHook hook) {
            if (!level.isClientSide) {
                int i = hook.retrieve(itemstack);
                ItemStack original = itemstack.copy();
                itemstack.hurtAndBreak(i, player, LivingEntity.getSlotForHand(hand));
                if (itemstack.isEmpty()) {
                    EventHooks.onPlayerDestroyItem(player, original, hand);
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (level instanceof ServerLevel) {
                ServerLevel serverlevel = (ServerLevel)level;
                int j = this.getFishingSpeed(player);
                int k = EnchantmentHelper.getFishingLuckBonus(serverlevel, itemstack, player) + (int)player.getLuck();
                level.addFreshEntity(new CustomFishingHook(player, level, k, j, this.lineColor));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private int getFishingSpeed(Player player) {
        int base = (int)(player.getAttributeValue(FishingAttributes.FISHING_SPEED) * 20.0F);
        if (this.biomeBonus != null) {
            if (player.level().getBiome(player.getOnPos()).is(this.biomeBonus)) {
                return base + this.speedBonus;
            }
        }
        return base;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (this.biomeBonus != null) {
            tooltip.add(Component.translatable(this.tooltipKey));
        }
    }
}
