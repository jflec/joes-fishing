package com.bigchadguys.fishing.screen;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.screen.custom.FishTrapMenu;
import com.bigchadguys.fishing.screen.custom.FishingMinigameMenu;
import com.bigchadguys.fishing.screen.custom.FishingLicenseMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, JoesFishing.MOD_ID);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static final DeferredHolder<MenuType<?>, MenuType<FishTrapMenu>> FISH_TRAP_MENU =
            registerMenuType("fish_trap_menu", FishTrapMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FishingMinigameMenu>> FISHING_MINIGAME_MENU =
            registerMenuType("fishing_minigame_menu", FishingMinigameMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FishingLicenseMenu>> FISHING_LICENSE_MENU =
            registerMenuType("fishing_license_menu", (containerId, playerInventory, buffer) ->
                    new FishingLicenseMenu(containerId, playerInventory)); // ✅ Corrected to match IContainerFactory

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
