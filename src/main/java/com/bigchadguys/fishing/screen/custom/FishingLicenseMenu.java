package com.bigchadguys.fishing.screen.custom;

import com.bigchadguys.fishing.capability.FishingStats;
import com.bigchadguys.fishing.capability.FishingStatsCapability;
import com.bigchadguys.fishing.network.FishingStatsSyncPacket;
import com.bigchadguys.fishing.screen.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static com.bigchadguys.fishing.network.FishingStatsCapability.*;

public class FishingLicenseMenu extends AbstractContainerMenu {
    private final FishingStats stats;

    public FishingLicenseMenu(int containerId, Inventory inv) {
        super(ModMenuTypes.FISHING_LICENSE_MENU.get(), containerId);
        Player player = inv.player;
        this.stats = FishingStatsCapability.get(player);

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new FishingStatsSyncPacket(stats.getFishingLevel(), stats.getFishingXp(), stats.getTotalFishCaught())
            );
        }
    }

    public int getFishingLevel() {
        return getClientFishingLevel();
    }

    public int getFishingXp() {
        return getClientFishingXp();
    }

    public int getTotalFishCaught() {
        return getClientTotalFishCaught();
    }

    public String[] getLast20Catches() {
        return stats.getLast20Catches();
    }

    public String[] getLeaderboard() {
        return stats.getLeaderboard();
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
