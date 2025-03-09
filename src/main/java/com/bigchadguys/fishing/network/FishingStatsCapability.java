package com.bigchadguys.fishing.network;

public class FishingStatsCapability {
    private static int clientFishingLevel = 1;
    private static int clientFishingXp = 0;
    private static int clientTotalFishCaught = 0;

    public static void setClientStats(int level, int xp, int totalCaught) {
        clientFishingLevel = level;
        clientFishingXp = xp;
        clientTotalFishCaught = totalCaught;
    }

    public static int getClientFishingLevel() {
        return clientFishingLevel;
    }

    public static int getClientFishingXp() {
        return clientFishingXp;
    }

    public static int getClientTotalFishCaught() {
        return clientTotalFishCaught;
    }
}
