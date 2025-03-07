package com.bigchadguys.fishing.item;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JoesFishing.MOD_ID);

    public static final Supplier<CreativeModeTab> MAYVIEW =
            CREATIVE_MODE_TABS.register("mayview_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fishing.fishing_tab"))
                    .icon(() -> new ItemStack(ModItems.WORM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WORM);
                        output.accept(ModItems.GUMMY_WORM);
                        output.accept(ModItems.TREASURE_CHEST);
                        output.accept(ModItems.HYDRITE_TREASURE_CHEST);

                        output.accept(ModItems.INTERMEDIATE_MATERIAL);
                        output.accept(ModItems.FISHING_LICENSE);

                        output.accept(ModBlocks.FISH_TRAP);

                        output.accept(ModItems.BEGINNER_FISHING_ROD);
                        output.accept(ModItems.INTERMEDIATE_FISHING_ROD);
                        output.accept(ModItems.ADVANCED_FISHING_ROD);
                        output.accept(ModItems.EXPERT_FISHING_ROD);
                        output.accept(ModItems.PROFESSIONAL_FISHING_ROD);
                        output.accept(ModItems.HYDRITE_FISHING_ROD);
                        output.accept(ModItems.REINFORCED_HYDRITE_FISHING_ROD);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
