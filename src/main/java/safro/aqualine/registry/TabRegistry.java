package safro.aqualine.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import safro.aqualine.Aqualine;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Aqualine.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("aqualine", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.aqualine"))
            .icon(() -> new ItemStack(ItemRegistry.STEEL_ROD.get()))
            .displayItems((parameters, output) -> ItemRegistry.ITEMS.getEntries().stream().map(DeferredHolder::value).forEach(output::accept)).build());
}
