package com.bigchadguys.fishing.item;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.item.custom.CustomFishingRodItem;
import com.bigchadguys.fishing.item.custom.FishingLicenseItem;
import com.bigchadguys.fishing.item.custom.HydriteTreasureChestItem;
import com.bigchadguys.fishing.item.custom.TreasureChestItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JoesFishing.MOD_ID);

    public static final DeferredItem<Item> BEGINNER_FISHING_ROD = registerRod("beginner_fishing_rod", 1, 1);
    public static final DeferredItem<Item> INTERMEDIATE_FISHING_ROD = registerRod("intermediate_fishing_rod", 2, 10);
    public static final DeferredItem<Item> ADVANCED_FISHING_ROD = registerRod("advanced_fishing_rod", 3, 20);
    public static final DeferredItem<Item> EXPERT_FISHING_ROD = registerRod("expert_fishing_rod", 4, 30);
    public static final DeferredItem<Item> PROFESSIONAL_FISHING_ROD = registerRod("professional_fishing_rod", 5, 50);
    public static final DeferredItem<Item> HYDRITE_FISHING_ROD = registerRod("hydrite_fishing_rod", 6, 75);
    public static final DeferredItem<Item> REINFORCED_HYDRITE_FISHING_ROD = registerRod("reinforced_hydrite_fishing_rod", 7, 75);

    public static final DeferredItem<Item> WORM = ITEMS.registerSimpleItem("worm");
    public static final DeferredItem<Item> GUMMY_WORM = ITEMS.registerSimpleItem("gummy_worm");
    public static final DeferredItem<Item> TREASURE_CHEST = ITEMS.registerItem("treasure_chest", TreasureChestItem::new);
    public static final DeferredItem<Item> HYDRITE_TREASURE_CHEST = ITEMS.registerItem("hydrite_treasure_chest", HydriteTreasureChestItem::new);
    public static final DeferredItem<Item> INTERMEDIATE_MATERIAL = ITEMS.registerSimpleItem("intermediate_material");
    public static final DeferredItem<Item> FISHING_LICENSE = ITEMS.registerItem("fishing_license", FishingLicenseItem::new);

    private static DeferredItem<Item> registerRod(String name, int tier, int requiredLevel) {
        return ITEMS.registerItem(name, properties -> new CustomFishingRodItem(tier, requiredLevel, new Item.Properties()));
    }

    public static List<Item> getCustomFishingRods() {
        return List.of(
                BEGINNER_FISHING_ROD.get(),
                INTERMEDIATE_FISHING_ROD.get(),
                ADVANCED_FISHING_ROD.get(),
                EXPERT_FISHING_ROD.get(),
                PROFESSIONAL_FISHING_ROD.get(),
                HYDRITE_FISHING_ROD.get(),
                REINFORCED_HYDRITE_FISHING_ROD.get()
        );
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
