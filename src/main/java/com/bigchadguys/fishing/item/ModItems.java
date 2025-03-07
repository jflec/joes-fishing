package com.bigchadguys.fishing.item;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.item.custom.CustomFishingRodItem;
import com.bigchadguys.fishing.item.custom.FishingLicenseItem;
import com.bigchadguys.fishing.item.custom.HydriteTreasureChestItem;
import com.bigchadguys.fishing.item.custom.TreasureChestItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JoesFishing.MOD_ID);

    public static final DeferredItem<Item> BEGINNER_FISHING_ROD = ITEMS.registerItem(
            "beginner_fishing_rod",
            properties -> new CustomFishingRodItem(1, new Item.Properties())
    );

    public static final DeferredItem<Item> INTERMEDIATE_FISHING_ROD = ITEMS.registerItem(
            "intermediate_fishing_rod",
            properties -> new CustomFishingRodItem(2, new Item.Properties())
    );

    public static final DeferredItem<Item> ADVANCED_FISHING_ROD = ITEMS.registerItem(
            "advanced_fishing_rod",
            properties -> new CustomFishingRodItem(3, new Item.Properties())
    );

    public static final DeferredItem<Item> EXPERT_FISHING_ROD = ITEMS.registerItem(
            "expert_fishing_rod",
            properties -> new CustomFishingRodItem(4, new Item.Properties())
    );

    public static final DeferredItem<Item> PROFESSIONAL_FISHING_ROD = ITEMS.registerItem(
            "professional_fishing_rod",
            properties -> new CustomFishingRodItem(5, new Item.Properties())
    );

    public static final DeferredItem<Item> HYDRITE_FISHING_ROD = ITEMS.registerItem(
            "hydrite_fishing_rod",
            properties -> new CustomFishingRodItem(6, new Item.Properties())
    );

    public static final DeferredItem<Item> REINFORCED_HYDRITE_FISHING_ROD = ITEMS.registerItem(
            "reinforced_hydrite_fishing_rod",
            properties -> new CustomFishingRodItem(7, new Item.Properties())
    );

    public static final DeferredItem<Item> WORM = ITEMS.registerSimpleItem("worm");
    public static final DeferredItem<Item> GUMMY_WORM = ITEMS.registerSimpleItem("gummy_worm");
    public static final DeferredItem<Item> TREASURE_CHEST = ITEMS.registerItem("treasure_chest", TreasureChestItem::new);
    public static final DeferredItem<Item> HYDRITE_TREASURE_CHEST = ITEMS.registerItem("hydrite_treasure_chest", HydriteTreasureChestItem::new);
    public static final DeferredItem<Item> INTERMEDIATE_MATERIAL = ITEMS.registerSimpleItem("intermediate_material");
    public static final DeferredItem<Item> FISHING_LICENSE = ITEMS.registerItem("fishing_license", FishingLicenseItem::new);

    public static List<Item> getCustomFishingRods() {
        return List.of(
                ModItems.BEGINNER_FISHING_ROD.get(),
                ModItems.ADVANCED_FISHING_ROD.get(),
                ModItems.EXPERT_FISHING_ROD.get(),
                ModItems.INTERMEDIATE_FISHING_ROD.get(),
                ModItems.PROFESSIONAL_FISHING_ROD.get(),
                ModItems.HYDRITE_FISHING_ROD.get(),
                ModItems.REINFORCED_HYDRITE_FISHING_ROD.get()
        );
    }


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
