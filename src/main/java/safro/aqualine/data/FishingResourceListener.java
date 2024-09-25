package safro.aqualine.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingManager;
import safro.aqualine.api.result.EnchantedItemFishResult;
import safro.aqualine.api.result.EntityFishResult;
import safro.aqualine.api.result.FishResult;
import safro.aqualine.api.result.ItemFishResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FishingResourceListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, FishResult> LOADED = new HashMap<>();
    private final RegistryAccess registryAccess;

    public FishingResourceListener(String directory, RegistryAccess registryAccess) {
        super(GSON, directory);
        this.registryAccess = registryAccess;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        LOADED.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            String type = GsonHelper.getAsString(object, "type");
//            String rarity = GsonHelper.getAsString(object, "rarity");

            if (type.equals("item")) {
                Optional<ItemFishResult> result = ConditionalOps.createConditionalCodec(ResultCodecs.ITEM).parse(JsonOps.INSTANCE, object).resultOrPartial(Aqualine.LOGGER::error).orElseThrow();
                result.ifPresent(itemFishResult -> LOADED.put(entry.getKey(), itemFishResult));
            } else if (type.equals("entity")) {
                Optional<EntityFishResult> result = ConditionalOps.createConditionalCodec(ResultCodecs.ENTITY).parse(JsonOps.INSTANCE, object).resultOrPartial(Aqualine.LOGGER::error).orElseThrow();
                result.ifPresent(r -> LOADED.put(entry.getKey(), r));
            } else if (type.equals("enchanted_item")) {
                Optional<EnchantedItemFishResult> result = ConditionalOps.createConditionalCodec(ResultCodecs.ENCHANTED_ITEM).parse(JsonOps.INSTANCE, object).resultOrPartial(Aqualine.LOGGER::error).orElseThrow();
                result.ifPresent(r -> LOADED.put(entry.getKey(), r));
            } else {
                Aqualine.LOGGER.error("Unable to load fishing result with no defined type: " + entry.getKey().toString());
            }
        }

        FishingManager.syncPools(LOADED.values());
    }

//    private ItemFishResult withCount(Item item, String rarity, JsonObject json) {
//        Pair<Integer, Integer> counts = ALUtil.getCount(json, "count");
//        return new ItemFishResult(item, rarity, counts.getFirst(), counts.getSecond());
//    }
//
//    private EnchantedItemFishResult withCountEnchanted(Item item, String rarity, JsonObject json) {
//        Pair<Integer, Integer> counts = ALUtil.getCount(json, "count");
//        ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(json, "tag"));
//        int levels = GsonHelper.getAsInt(json, "levels");
//        return new EnchantedItemFishResult(item, TagKey.create(Registries.ENCHANTMENT, tagId), levels, rarity, counts.getFirst(), counts.getSecond());
//    }
}
