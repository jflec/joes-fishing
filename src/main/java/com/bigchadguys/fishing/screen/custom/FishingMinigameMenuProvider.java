package com.bigchadguys.fishing.screen.custom;

import com.bigchadguys.fishing.network.ClientRodTierDataHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishingMinigameMenuProvider implements MenuProvider {
    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Fishing Minigame");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new FishingMinigameMenu(containerId, playerInventory, ClientRodTierDataHandler.rodTier);
    }
}
