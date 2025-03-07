package com.bigchadguys.fishing.util;

import com.bigchadguys.fishing.item.custom.FishingEventHandler;

public class FishingMinigameUtil {
    public static int getHardcodedFishAtlasWidth(int rodTier) {
        return 48 + 12 * (rodTier - 1);
    }

    public static String getTierFolder(int rodTier) {
        return switch (rodTier) {
            case 2 -> "tier_two_fish";
            case 3 -> "tier_three_fish";
            case 4 -> "tier_four_fish";
            case 5 -> "tier_five_fish";
            case 6 -> "tier_six_fish";
            case 7 -> "tier_seven_fish";
            default -> "tier_one_fish";
        };
    }

    public static float getDifficultyMultiplier(FishingEventHandler.FishRarity rarity) {
        return switch (rarity) {
            case COMMON -> 1.0f;
            case UNCOMMON -> 1.1f;
            case RARE -> 1.2f;
            case EPIC -> 1.3f;
            case EXOTIC -> 1.4f;
            case LEGENDARY -> 1.5f;
            case TRANSCENDENT -> 1.6f;
            case MYTHICAL -> 1.7f;
            case DIVINE -> 1.8f;
            case CELESTIAL -> 1.9f;
            case ETERNAL -> 2.0f;
        };
    }
}
