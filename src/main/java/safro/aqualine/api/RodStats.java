package safro.aqualine.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashMap;
import java.util.Map;

public class RodStats {
    private final Map<String, Integer> stats = new HashMap<>();

    private RodStats() {
    }

    public static RodStats create() {
        return new RodStats();
    }

    public RodStats with(String name, int stat) {
        this.stats.put(name, stat);
        return this;
    }

    public int get(String name) {
        return this.stats.get(name);
    }

    public int getOrNone(String name) {
        return this.has(name) ? this.get(name) : 0;
    }

    public boolean has(String name) {
        return this.stats.containsKey(name);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("Stats")) {
            this.stats.clear();
            ListTag list = tag.getList("Stats", CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag next = list.getCompound(i);
                this.stats.put(next.getString("Name"), next.getInt("Stat"));
            }
        }
    }

    public void write(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<String, Integer> entry : this.stats.entrySet()) {
            CompoundTag next = new CompoundTag();
            next.putString("Name", entry.getKey());
            next.putInt("Stat", entry.getValue());
            list.add(next);
        }
        tag.put("Stats", list);
    }
}
