package safro.aqualine.util;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.GsonHelper;

public class ALUtil {

    public static Pair<Integer, Integer> getCount(JsonObject json, String name) {
        if (json.has(name) && json.get(name).isJsonObject()) {
            JsonObject obj = json.getAsJsonObject(name);
            int min = Math.max(1, GsonHelper.getAsInt(obj, "min", 1));
            int max = GsonHelper.getAsInt(obj, "max", 1);
            return Pair.of(min, max);
        } else {
            int count = GsonHelper.getAsInt(json, name, 1);
            return Pair.of(count, count);
        }
    }
}
