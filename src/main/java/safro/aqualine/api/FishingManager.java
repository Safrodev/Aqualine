package safro.aqualine.api;

import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.jetbrains.annotations.Nullable;
import safro.aqualine.AqualineConfig;
import safro.aqualine.api.result.EntityFishResult;
import safro.aqualine.api.result.FishResult;
import safro.aqualine.api.result.ItemFishResult;
import safro.aqualine.entity.CustomFishingHook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FishingManager {
    public static final ArrayList<ItemFishResult> LOOT = new ArrayList<>();
    public static final ArrayList<EntityFishResult> ENTITIES = new ArrayList<>();

    public static void syncPools(Collection<FishResult> results) {
        LOOT.clear();
        ENTITIES.clear();
        results.forEach(fishResult -> {
            if (fishResult instanceof ItemFishResult itemFishResult) {
                LOOT.add(itemFishResult);
            } else if (fishResult instanceof EntityFishResult entityFishResult) {
                ENTITIES.add(entityFishResult);
            }
        });
    }

    public static boolean handleRandomFish(ServerLevel level, Player player, CustomFishingHook hook) {
        if (!LOOT.isEmpty() && !ENTITIES.isEmpty()) {
            FishResult result = selectRandom(level.getRandom(), hook.luck);
            if (result != null) {
                result.execute(level, player, hook);
                player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5, player.getZ() + 0.5, hook.getRandom().nextInt(6) + 1));
                FishingLevel.onFish(player);
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static FishResult selectRandom(RandomSource random, int luck) {
        float entityWeight = (float) AqualineConfig.baseEntityChance / 100.0F;
        if (random.nextFloat() < entityWeight) {
            Optional<EntityFishResult> optional = selectRandomFrom(ENTITIES, random, luck);
            return optional.orElse(null);
        } else {
            Optional<ItemFishResult> optional = selectRandomFrom(LOOT, random, luck);
            return optional.orElse(null);
        }
    }

    private static <T extends FishResult> Optional<T> selectRandomFrom(List<T> list, RandomSource random, int luck) {
        SimpleWeightedRandomList.Builder<T> builder = SimpleWeightedRandomList.builder();
        list.forEach(result -> builder.add(result, getWeight(result, luck)));
        return builder.build().getRandomValue(random);
    }

    private static int getWeight(FishResult result, int luck) {
        int base = result.getRarity().getWeight();
        return (result.getRarity().equals(FishResult.Rarity.RARE) || result.getRarity().equals(FishResult.Rarity.LEGENDARY)) ? base + (luck * 3) : base;
    }
}
