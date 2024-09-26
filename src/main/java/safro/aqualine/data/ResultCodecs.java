package safro.aqualine.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import safro.aqualine.api.result.EnchantedItemFishResult;
import safro.aqualine.api.result.EntityFishResult;
import safro.aqualine.api.result.ItemFishResult;

import java.util.function.Function;

public class ResultCodecs {
    public static final Codec<ItemFishResult> ITEM = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.comapFlatMap(
                    name -> BuiltInRegistries.ITEM.containsKey(name) ? DataResult.success(BuiltInRegistries.ITEM.get(name)) : DataResult.error(() -> "Could not find entity type: " + name),
                    BuiltInRegistries.ITEM::getKey).fieldOf("item").forGetter(ItemFishResult::item),
            Codec.STRING.fieldOf("rarity").forGetter(ItemFishResult::rarityRaw),
            IntProvider.validateCodec(1, Integer.MAX_VALUE, UniformInt.CODEC.codec()).optionalFieldOf("count", UniformInt.of(1, 1)).forGetter(ItemFishResult::range)
    ).apply(instance, ItemFishResult::new));

    public static final Codec<EnchantedItemFishResult> ENCHANTED_ITEM = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.comapFlatMap(
                    name -> BuiltInRegistries.ITEM.containsKey(name) ? DataResult.success(BuiltInRegistries.ITEM.get(name)) : DataResult.error(() -> "Could not find entity type: " + name),
                    BuiltInRegistries.ITEM::getKey).fieldOf("item").forGetter(EnchantedItemFishResult::item),
            Codec.STRING.fieldOf("rarity").forGetter(EnchantedItemFishResult::rarityRaw),
            IntProvider.validateCodec(1, Integer.MAX_VALUE, UniformInt.CODEC.codec()).optionalFieldOf("count", UniformInt.of(1, 1)).forGetter(EnchantedItemFishResult::range),
            ResourceLocation.CODEC.fieldOf("tag").forGetter(EnchantedItemFishResult::tagKey),
            Codec.INT.fieldOf("levels").forGetter(EnchantedItemFishResult::levels)
    ).apply(instance, EnchantedItemFishResult::new));

    public static final Codec<EntityFishResult> ENTITY = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.comapFlatMap(
                    name -> BuiltInRegistries.ENTITY_TYPE.containsKey(name) ? DataResult.success(name) : DataResult.error(() -> "Could not find entity type: " + name),
                    Function.identity()).fieldOf("entity").forGetter(EntityFishResult::getEntityId),
            Codec.STRING.fieldOf("rarity").forGetter(EntityFishResult::rarityRaw)
    ).apply(instance, EntityFishResult::new));
}
