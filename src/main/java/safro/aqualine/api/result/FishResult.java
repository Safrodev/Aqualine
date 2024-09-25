package safro.aqualine.api.result;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.aqualine.AqualineConfig;
import safro.aqualine.entity.projectile.CustomFishingHook;

public abstract class FishResult {
    private final Rarity rarity;

    public FishResult(String rarity) {
        this(Rarity.from(rarity));
    }

    public FishResult(Rarity rarity) {
        this.rarity = rarity;
    }

    /**
     * Runs fish result after a player successfully catches something
     * @param world - Player's world (server-side)
     * @param player - Catcher
     * @param hook - The fishing hook used
     */
    public abstract void execute(ServerLevel world, Player player, CustomFishingHook hook);

    /**
     * @return - The stack to display for JEI fishing loot
     */
    public abstract ItemStack getDisplayStack();

    public Rarity getRarity() {
        return rarity;
    }

    public String rarityRaw() {
        return this.rarity.name();
    }

    public enum Rarity {
        COMMON(AqualineConfig.commonWeight, "text.aqualine.rarity.common"),
        UNCOMMON(AqualineConfig.uncommonWeight, "text.aqualine.rarity.uncommon"),
        RARE(AqualineConfig.rareWeight, "text.aqualine.rarity.rare"),
        LEGENDARY(AqualineConfig.legendaryWeight, "text.aqualine.rarity.legendary");

        private final int weight;
        private final String key;

        Rarity(int weight, String key) {
            this.weight = weight;
            this.key = key;
        }

        public static Rarity from(String str) {
            return Rarity.valueOf(str.toUpperCase());
        }

        public int getWeight() {
            return this.weight;
        }

        public String getTranslatedName() {
            return Component.translatable(this.key).getString();
        }
    }
}
