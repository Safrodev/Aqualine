package safro.aqualine.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingManager;
import safro.aqualine.api.result.EntityFishResult;
import safro.aqualine.api.result.FishResult;
import safro.aqualine.api.result.ItemFishResult;

import java.util.HashMap;
import java.util.Map;

public class FishingResourceListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, FishResult> LOADED = new HashMap<>();

    public FishingResourceListener(String directory) {
        super(GSON, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        LOADED.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            JsonObject object = entry.getValue().getAsJsonObject();
            String type = GsonHelper.getAsString(object, "type");
            String rarity = GsonHelper.getAsString(object, "rarity");

            if (type.equals("item")) {
                String name = GsonHelper.getAsString(object, "item");
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(name));
                LOADED.put(entry.getKey(), withCount(item, rarity, object));
            } else if (type.equals("entity")) {
                String name = GsonHelper.getAsString(object, "entity");
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(name));
                LOADED.put(entry.getKey(), new EntityFishResult(entityType, rarity));
            } else {
                Aqualine.LOGGER.error("Unable to load fishing result with no defined type: " + entry.getKey().toString());
            }
        }

        FishingManager.syncPools(LOADED.values());
    }

    private static ItemFishResult withCount(Item item, String rarity, JsonObject json) {
        if (json.has("count") && json.get("count").isJsonObject()) {
            JsonObject obj = json.getAsJsonObject("count");
            int min = Math.max(1, GsonHelper.getAsInt(obj, "min", 1));
            int max = GsonHelper.getAsInt(obj, "max", 1);
            return new ItemFishResult(item, rarity, min, max);
        }
        int count = GsonHelper.getAsInt(json, "count", 1);
        return new ItemFishResult(item, rarity, count, count);
    }
}
