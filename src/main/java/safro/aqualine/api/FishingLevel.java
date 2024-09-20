package safro.aqualine.api;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import safro.aqualine.Aqualine;
import safro.aqualine.AqualineConfig;

public class FishingLevel implements INBTSerializable<CompoundTag> {
    private static final ResourceLocation MODIFIER_ID = Aqualine.id("fishing_level_boost");
    private int level;
    private int xpToNext;
    private int xp;

    public FishingLevel() {
        this.level = 0;
        this.xp = 0;
        this.xpToNext = 5;
    }

    public static void onFish(Player player) {
        FishingLevel data = player.getData(Aqualine.FISHING_LEVEL);
        if (data.level < AqualineConfig.maxFishingLevel) {
            data.xp++;
            if (data.xp >= data.xpToNext) {
                data.level++;
                data.xpToNext = calcXP(data.level);
                data.xp = 0;
                player.sendSystemMessage(Component.translatable("text.aqualine.level_up", data.level).withStyle(ChatFormatting.GREEN));
                player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                data.updateHealthBonus(player);
            } else {
                player.displayClientMessage(Component.translatable("text.aqualine.fish_xp", data.xp, data.xpToNext).withColor(0x4db7e9), true);
            }
            player.setData(Aqualine.FISHING_LEVEL, data);
        }
    }

    public static void copy(Player old, Player current) {
        FishingLevel prev = old.getData(Aqualine.FISHING_LEVEL);
        current.getData(Aqualine.FISHING_LEVEL).level = prev.level;
        current.getData(Aqualine.FISHING_LEVEL).xp = prev.xp;
        current.getData(Aqualine.FISHING_LEVEL).xpToNext = prev.xpToNext;
    }

    public static int calcXP(int level) {
        return (int)Math.floor(6 + Math.pow(level, 1.75));
    }

    public void updateHealthBonus(Player player) {
        int hearts = this.level / 5;
        if (hearts > 0) {
            AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, 2.0 * hearts, AttributeModifier.Operation.ADD_VALUE);
            player.getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(modifier);
            player.setHealth(player.getHealth());
        } else {
            player.getAttribute(Attributes.MAX_HEALTH).removeModifier(MODIFIER_ID);
        }
    }

    public void setTo(int level) {
        this.level = level;
        this.xp = 0;
        this.xpToNext = calcXP(level);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Level", this.level);
        tag.putInt("XpToNext", this.xpToNext);
        tag.putInt("CurrentXP", this.xp);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.level = tag.getInt("Level");
        this.xpToNext = tag.getInt("XpToNext");
        this.xp = tag.getInt("CurrentXP");
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToNext() {
        return xpToNext;
    }
}
