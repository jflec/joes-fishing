package com.bigchadguys.fishing.capability;

import com.bigchadguys.fishing.data.FishingLeaderboard;
import com.bigchadguys.fishing.item.custom.FishingEventHandler.FishRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FishingStats implements INBTSerializable<CompoundTag> {
    private final Map<FishRarity, Integer> fishCaught = new EnumMap<>(FishRarity.class);
    private int fishingLevel = 1;
    private int fishingXp = 0;
    private final List<String> last20Catches = new LinkedList<>();

    public int getFishCaught(FishRarity rarity) {
        return fishCaught.getOrDefault(rarity, 0);
    }

    public void addFish(FishRarity rarity) {
        fishCaught.put(rarity, getFishCaught(rarity) + 1);
        addRecentCatch(rarity.name());
    }

    public int getFishingLevel() {
        return fishingLevel;
    }

    public int getFishingXp() {
        return fishingXp;
    }

    public void setFishingLevel(int newLevel) {
        this.fishingLevel = newLevel;
        // Optionally reset XP or adjust it based on your level-up formula
        this.fishingXp = 0;
    }

    public int getTotalFishCaught() {
        return fishCaught.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void addRecentCatch(String fishName) {
        if (last20Catches.size() >= 20) {
            last20Catches.removeFirst();
        }
        last20Catches.add(fishName);
    }

    public String[] getLast20Catches() {
        return last20Catches.toArray(new String[0]);
    }

    public String[] getLeaderboard() {
        return FishingLeaderboard.getTop10Players(); // ✅ Correct static method call
    }

    public void addFishingXp(int amount, ServerPlayer player) {
        fishingXp += amount;
        checkLevelUp(player);
    }

    private void checkLevelUp(ServerPlayer player) {
        int oldLevel = fishingLevel;
        while (fishingXp >= getXpForNextLevel()) {
            fishingXp -= getXpForNextLevel();
            fishingLevel++;
        }

        if (fishingLevel > oldLevel) {
            player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);

            player.displayClientMessage(
                    Component.literal("Fishing level increased from " + oldLevel + " to " + fishingLevel + "!"),
                    true
            );
            FishingLeaderboard.get().updatePlayerLevel(player, fishingLevel);
        }

    }

    public int getXpForNextLevel() {
        return (50 * (fishingLevel * fishingLevel)) - (50 * fishingLevel);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<FishRarity, Integer> entry : fishCaught.entrySet()) {
            tag.putInt(entry.getKey().name(), entry.getValue());
        }
        tag.putInt("FishingLevel", fishingLevel);
        tag.putInt("FishingXp", fishingXp);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        for (FishRarity rarity : FishRarity.values()) {
            if (tag.contains(rarity.name())) {
                fishCaught.put(rarity, tag.getInt(rarity.name()));
            }
        }
        this.fishingLevel = tag.getInt("FishingLevel");
        this.fishingXp = tag.getInt("FishingXp");
    }
}
