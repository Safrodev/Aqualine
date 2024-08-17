package safro.aqualine;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;
import safro.aqualine.api.FishingAttributes;
import safro.aqualine.api.FishingLevel;
import safro.aqualine.network.NetworkHandler;
import safro.aqualine.registry.EntityRegistry;
import safro.aqualine.registry.ItemRegistry;
import safro.aqualine.registry.TabRegistry;

import java.util.function.Supplier;

@Mod(Aqualine.MODID)
public class Aqualine {
    public static final String MODID = "aqualine";
    public static final Logger LOGGER = LogUtils.getLogger();
    // Data Attachments
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<FishingLevel>> FISHING_LEVEL = ATTACHMENT_TYPES.register("fishing_level", () -> AttachmentType.serializable(FishingLevel::new).build());

    public Aqualine(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NetworkHandler::register);
        modEventBus.addListener(FishingAttributes::applyAttributes);

//        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, CommonEvents::onFish);

        ATTACHMENT_TYPES.register(modEventBus);
        FishingAttributes.ATTRIBUTES.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, AqualineConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
