package com.bigchadguys.fishing.data;

import com.bigchadguys.fishing.JoesFishing;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JoesFishing.MOD_ID);

    public static final Supplier<DataComponentType<String>> FISH_RARITY =
            DATA_COMPONENT_TYPES.register("fish_rarity", () ->
                    DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .build()
            );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
