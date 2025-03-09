package com.bigchadguys.fishing.item;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JoesFishing.MOD_ID);

    public static final Supplier<CreativeModeTab> FISHING = CREATIVE_MODE_TABS.register("fishing_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fishing.fishing_tab"))
                    .icon(() -> new ItemStack(ModItems.WORM.get()))
                    .displayItems((parameters, output) -> {
                        List.of(
                                ModItems.WORM, ModItems.GUMMY_WORM, ModItems.TREASURE_CHEST, ModItems.HYDRITE_TREASURE_CHEST,
                                ModItems.INTERMEDIATE_MATERIAL, ModItems.FISHING_LICENSE, ModBlocks.FISH_TRAP,
                                ModItems.BEGINNER_FISHING_ROD, ModItems.INTERMEDIATE_FISHING_ROD, ModItems.ADVANCED_FISHING_ROD,
                                ModItems.EXPERT_FISHING_ROD, ModItems.PROFESSIONAL_FISHING_ROD,
                                ModItems.HYDRITE_FISHING_ROD, ModItems.REINFORCED_HYDRITE_FISHING_ROD
                        ).forEach(output::accept);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
