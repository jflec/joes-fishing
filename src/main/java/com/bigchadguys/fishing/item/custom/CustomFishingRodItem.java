package com.bigchadguys.fishing.item.custom;

import com.bigchadguys.fishing.capability.FishingStats;
import com.bigchadguys.fishing.capability.FishingStatsCapability;
import com.bigchadguys.fishing.entity.custom.CustomFishingBobberEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomFishingRodItem extends FishingRodItem {
    private final int rodTier;
    private final int requiredLevel;

    public CustomFishingRodItem(int rodTier, int requiredLevel, Item.Properties properties) {
        super(properties);
        this.rodTier = rodTier;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal("Requires Fishing level: " + requiredLevel)
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        boolean isClientSide = level.isClientSide();
        int playerFishingLevel = level.isClientSide()
                ? FishingStatsCapability.getClientFishingLevel() // ✅ Use client-synced data
                : FishingStatsCapability.get(player).getFishingLevel(); // ✅ Use real data on server


        if (isClientSide) {
            System.out.println("CLIENT fishing level: " + playerFishingLevel);
        } else {
            System.out.println("SERVER fishing level: " + playerFishingLevel);
        }

        ItemStack itemstack = player.getItemInHand(hand);

        if (playerFishingLevel < this.requiredLevel) {
            System.out.println(playerFishingLevel);
            System.out.println(this.requiredLevel);

            if (!isClientSide) {
                player.displayClientMessage(
                        Component.literal("Your Fishing level is too low!"),
                        true
                );
            }
            System.out.println("fail");
            return InteractionResultHolder.fail(itemstack);
        }

        if (player.fishing != null) {
            if (!isClientSide) {
                int i = player.fishing.retrieve(itemstack);
                itemstack.hurtAndBreak(i, (ServerLevel) level, player, p -> {});
            }
            float pitch = 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
                    1.0F, pitch);
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            float pitch = 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
                    0.5F, pitch);
            if (level instanceof ServerLevel serverLevel) {
                int j = (int) (EnchantmentHelper.getFishingTimeReduction(serverLevel, itemstack, player) * 20.0F);
                int k = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemstack, player);
                CustomFishingBobberEntity bobber = new CustomFishingBobberEntity(player, level, k, j, this.rodTier);
                level.addFreshEntity(bobber);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, isClientSide);
    }
}
