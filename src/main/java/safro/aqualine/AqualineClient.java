package safro.aqualine;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import safro.aqualine.client.model.AnchorModel;
import safro.aqualine.client.render.ALHumanoidRenderer;
import safro.aqualine.client.render.AnchorRenderer;
import safro.aqualine.client.render.CustomFishingHookRenderer;
import safro.aqualine.client.render.GhostCaptainRenderer;
import safro.aqualine.registry.EntityRegistry;
import safro.aqualine.registry.ItemRegistry;

@EventBusSubscriber(modid = Aqualine.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AqualineClient {
    public static final ModelLayerLocation ANCHOR_LAYER = new ModelLayerLocation(Aqualine.id("anchor"), "main");

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.FISHING_HOOK.get(), CustomFishingHookRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ANCHOR.get(), AnchorRenderer::new);

        event.registerEntityRenderer(EntityRegistry.BUCCANEER.get(), context -> new ALHumanoidRenderer<>(ResourceLocation.fromNamespaceAndPath(Aqualine.MODID, "textures/entity/headless_buccaneer.png"), context));
        event.registerEntityRenderer(EntityRegistry.GHOST_CAPTAIN.get(), GhostCaptainRenderer::new);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ANCHOR_LAYER, AnchorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            rodProperty(ItemRegistry.STEEL_ROD.get());
            rodProperty(ItemRegistry.FROZEN_ROD.get());
            rodProperty(ItemRegistry.CRYSTAL_ROD.get());
            rodProperty(ItemRegistry.UNDEAD_ROD.get());
            rodProperty(ItemRegistry.ANGLER_ROD.get());
            rodProperty(ItemRegistry.DRAGONFIN_ROD.get());
        });
    }

    private static void rodProperty(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("cast"), (p_174585_, p_174586_, p_174587_, p_174588_) -> {
            if (p_174587_ == null) {
                return 0.0F;
            } else {
                boolean flag = p_174587_.getMainHandItem() == p_174585_;
                boolean flag1 = p_174587_.getOffhandItem() == p_174585_;
                if (p_174587_.getMainHandItem().getItem() instanceof FishingRodItem) {
                    flag1 = false;
                }

                return (flag || flag1) && p_174587_ instanceof Player && ((Player)p_174587_).fishing != null ? 1.0F : 0.0F;
            }
        });
    }
}
