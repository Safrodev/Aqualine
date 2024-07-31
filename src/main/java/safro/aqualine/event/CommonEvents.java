package safro.aqualine.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import safro.aqualine.Aqualine;
import safro.aqualine.api.FishingLevel;
import safro.aqualine.api.FishingManager;
import safro.aqualine.data.FishingResourceListener;

@EventBusSubscriber(modid = Aqualine.MODID)
public class CommonEvents {

    @SubscribeEvent
    public static void onFish(ItemFishedEvent event) {
        Player player = event.getEntity();
        FishingHook hook = event.getHookEntity();
//        if (player.getMainHandItem().getItem() instanceof AqualineRod || player.getOffhandItem().getItem() instanceof AqualineRod) {
            if (!hook.level().isClientSide()) {
                ServerLevel level = (ServerLevel) hook.level();
                if (FishingManager.handleRandomFish(level, player, hook)) {
                    event.damageRodBy(1);
                    event.setCanceled(true);
                }
            }
//        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getOriginal().hasData(Aqualine.FISHING_LEVEL)) {
            FishingLevel.copy(event.getOriginal(), event.getEntity());
            event.getEntity().getData(Aqualine.FISHING_LEVEL).updateHealthBonus(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void addListeners(AddReloadListenerEvent event) {
        event.addListener(new FishingResourceListener("fishing"));
    }
}
