package safro.aqualine.api.result;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

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
    public abstract void execute(ServerLevel world, Player player, FishingHook hook);

    public Rarity getRarity() {
        return rarity;
    }

    public enum Rarity {
        COMMON(55),
        UNCOMMON(30),
        RARE(13),
        LEGENDARY(2);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        public static Rarity from(String str) {
            return Rarity.valueOf(str.toUpperCase());
        }

        public int getWeight() {
            return this.weight;
        }
    }
}
