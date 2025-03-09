package com.bigchadguys.fishing.item.custom;


import com.bigchadguys.fishing.screen.custom.FishingLicenseMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FishingLicenseItem extends Item {
    public FishingLicenseItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new FishingLicenseMenu(containerId, playerInventory),
                    Component.literal("Fishing License")
            );

            serverPlayer.openMenu(provider); // ✅ Correctly opens Fishing License Menu
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

}