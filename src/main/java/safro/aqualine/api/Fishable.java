package safro.aqualine.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

// Entities that can be fished up can implement this interface to define custom behavior after reel
public interface Fishable {
    void onFished(ServerLevel world, Player player);
}
