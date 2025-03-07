package com.bigchadguys.fishing.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import com.bigchadguys.fishing.entity.custom.CustomFishingBobberEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CustomFishingRodItem extends FishingRodItem {
    private final int rodTier;

    public CustomFishingRodItem(int rodTier, Item.Properties properties) {
        super(properties);
        this.rodTier = rodTier;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.fishing != null) {
            if (!level.isClientSide()) {
                int i = player.fishing.retrieve(itemstack);
                itemstack.hurtAndBreak(i, (ServerLevel) level, player, item -> {});
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
                    1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (level instanceof ServerLevel serverLevel) {
                int j = (int) (EnchantmentHelper.getFishingTimeReduction(serverLevel, itemstack, player) * 20.0F);
                int k = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemstack, player);
                CustomFishingBobberEntity bobber = new CustomFishingBobberEntity(player, level, k, j, this.rodTier);
                level.addFreshEntity(bobber);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
