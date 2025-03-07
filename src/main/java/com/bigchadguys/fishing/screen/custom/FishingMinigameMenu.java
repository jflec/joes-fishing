package com.bigchadguys.fishing.screen.custom;

import com.bigchadguys.fishing.network.ClientRodTierDataHandler;
import com.bigchadguys.fishing.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FishingMinigameMenu extends AbstractContainerMenu {
    private final int rodTier;

    public FishingMinigameMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, ClientRodTierDataHandler.rodTier);
    }

    public FishingMinigameMenu(int containerId, Inventory inv, int rodTier) {
        super(ModMenuTypes.FISHING_MINIGAME_MENU.get(), containerId);
        this.rodTier = rodTier;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    public int getRodTier() {
        return rodTier;
    }
}
