package com.bigchadguys.fishing.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FishingLeaderboard extends SavedData {
    private static final String DATA_NAME = "fishing_leaderboard";
    private final Map<UUID, Integer> playerLevels = new HashMap<>();

    public static FishingLeaderboard get() { // ✅ No parameters
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        assert server != null;
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(new SavedData.Factory<>(FishingLeaderboard::new, FishingLeaderboard::load), DATA_NAME);
    }

    public void updatePlayerLevel(ServerPlayer player, int level) {
        playerLevels.put(player.getUUID(), level);
        setDirty();
    }

    public static String[] getTop10Players() {
        FishingLeaderboard leaderboard = get();
        List<Map.Entry<UUID, Integer>> sorted = leaderboard.getSortedLeaderboard();
        return sorted.stream()
                .limit(10)
                .map(entry -> "Player " + entry.getKey() + ": Level " + entry.getValue())
                .toArray(String[]::new);
    }


    public List<Map.Entry<UUID, Integer>> getSortedLeaderboard() {
        List<Map.Entry<UUID, Integer>> sortedList = new ArrayList<>(playerLevels.entrySet());
        sortedList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return sortedList;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Integer> entry : playerLevels.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            playerTag.putInt("Level", entry.getValue());
            list.add(playerTag);
        }
        tag.put("Players", list);
        return tag;
    }

    public static FishingLeaderboard load(CompoundTag tag, HolderLookup.Provider registries) {
        FishingLeaderboard leaderboard = new FishingLeaderboard();
        ListTag list = tag.getList("Players", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag playerTag = (CompoundTag) t;
            leaderboard.playerLevels.put(playerTag.getUUID("UUID"), playerTag.getInt("Level"));
        }
        return leaderboard;
    }
}