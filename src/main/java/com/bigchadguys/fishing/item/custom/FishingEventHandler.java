package com.bigchadguys.fishing.item.custom;

import com.bigchadguys.fishing.capability.FishingStats;
import com.bigchadguys.fishing.capability.FishingStatsCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Set;
import java.util.function.Consumer;

import static com.bigchadguys.fishing.data.ModDataComponents.FISH_RARITY;

public class FishingEventHandler {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Set<FishRarity> BROADCAST_RARITIES = Set.of(FishRarity.TRANSCENDENT, FishRarity.DIVINE, FishRarity.MYTHICAL, FishRarity.CELESTIAL, FishRarity.ETERNAL);
    private static final java.util.Map<FishRarity, String> FISH_RARITY_ODDS = java.util.Map.of(
            FishRarity.TRANSCENDENT, "1 in 25,000",
            FishRarity.DIVINE, "1 in 50,000",
            FishRarity.MYTHICAL, "1 in 100,000",
            FishRarity.CELESTIAL, "1 in 250,000",
            FishRarity.ETERNAL, "1 in 1,000,000"
    );

    public static FishRarity currentFishRarity = null;

    public static FishRarity determineFishRarity() {
        return FishRarity.values()[RANDOM.nextInt(FishRarity.values().length)];
    }

    private static void awardLootInternal(Player player, ResourceKey<LootTable> lootTableKey, Consumer<ItemStack> postProcessor) {
        Level level = player.level();
        ItemStack fishingRod = player.getMainHandItem();
        if (!(fishingRod.getItem() instanceof CustomFishingRodItem) || !(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();
        Holder<LootTable> lootTableHolder = server.reloadableRegistries().lookup()
                .get(Registries.LOOT_TABLE, lootTableKey)
                .orElseThrow(() -> new IllegalStateException("Missing loot table: " + lootTableKey.location()));
        LootTable lootTable = lootTableHolder.value();

        LootParams params = new LootParams.Builder(serverLevel)
                .withLuck(player.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.TOOL, fishingRod)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .create(LootContextParamSets.FISHING);

        for (ItemStack item : lootTable.getRandomItems(params)) {
            postProcessor.accept(item);
            if (!player.addItem(item)) player.drop(item, false);
        }
    }

    public static void awardLoot(ServerPlayer serverPlayer) {
        FishRarity rarity = (currentFishRarity != null) ? currentFishRarity : determineFishRarity();
        currentFishRarity = null;

        awardLootInternal(serverPlayer, BuiltInLootTables.FISHING_FISH, item -> {
            if (isFish(item)) {
                applyFishRarityToItem(item, rarity);

                FishingStats stats = FishingStatsCapability.get(serverPlayer);
                stats.addFish(rarity);

                int xpGained = getXpForRarity(rarity);
                stats.addFishingXp(xpGained, serverPlayer); // ✅ Now passes serverPlayer to send the level-up message

                if (BROADCAST_RARITIES.contains(rarity)) {
                    broadcastMessage(serverPlayer.level(), serverPlayer, getFishName(item), rarity);
                    playSuccessEffects(serverPlayer.level(), serverPlayer);
                } else {
                    String message = "You caught " + getIndefiniteArticle(rarity) + " " + rarity.getDisplayName() + " " + getFishName(item) + "!";
                    serverPlayer.sendSystemMessage(Component.literal(message).withStyle(rarity.getColor()));
                }

            }
        });
    }

    private static int getXpForRarity(FishRarity rarity) {
        return switch (rarity) {
            case COMMON -> 10;
            case UNCOMMON -> 20;
            case RARE -> 50;
            case EPIC -> 100;
            case EXOTIC -> 250;
            case LEGENDARY -> 500;
            case TRANSCENDENT -> 1000;
            case MYTHICAL -> 2000;
            case DIVINE -> 4000;
            case CELESTIAL -> 8000;
            case ETERNAL -> 16000;
        };
    }

    public static void awardJunk(Player player) {
        awardLootInternal(player, BuiltInLootTables.FISHING_JUNK, item -> {});
    }

    private static boolean isFish(ItemStack item) {
        return item.is(Items.COD) || item.is(Items.SALMON) || item.is(Items.TROPICAL_FISH) || item.is(Items.PUFFERFISH);
    }

    private static void applyFishRarityToItem(ItemStack item, FishRarity rarity) {
        item.set(FISH_RARITY.get(), rarity.name());
        item.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                Component.literal(rarity.getDisplayName() + " " + getFishName(item))
                        .withStyle(style -> style.withColor(rarity.getColor()).withItalic(false))
        );
    }

    private static String getFishName(ItemStack item) {
        return item.is(Items.COD) ? "Cod" :
                item.is(Items.SALMON) ? "Salmon" :
                        item.is(Items.TROPICAL_FISH) ? "Tropical Fish" :
                                item.is(Items.PUFFERFISH) ? "Pufferfish" : "Fish";
    }

    private static void broadcastMessage(Level level, Player player, String fishName, FishRarity rarity) {
        String message = player.getName().getString() + " caught " + getIndefiniteArticle(rarity) + " " + rarity.getDisplayName() + " " + fishName + "!";

        if (FISH_RARITY_ODDS.containsKey(rarity)) {
            message += " (" + FISH_RARITY_ODDS.get(rarity) + ")";
            MinecraftServer server = ((ServerLevel) level).getServer();
            server.getPlayerList().broadcastSystemMessage(Component.literal(message).withStyle(rarity.getColor()), false);
        }
    }

    private static String getIndefiniteArticle(FishRarity rarity) {
        return "AEIOU".indexOf(rarity.getDisplayName().charAt(0)) != -1 ? "an" : "a";
    }

    private static void playSuccessEffects(Level level, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
        }
    }

    public enum FishRarity {
        COMMON("Common", ChatFormatting.WHITE),
        UNCOMMON("Uncommon", ChatFormatting.GREEN),
        RARE("Rare", ChatFormatting.BLUE),
        EPIC("Epic", ChatFormatting.DARK_PURPLE),
        EXOTIC("Exotic", ChatFormatting.RED),
        LEGENDARY("Legendary", ChatFormatting.GOLD),
        TRANSCENDENT("Transcendent", ChatFormatting.DARK_BLUE),
        MYTHICAL("Mythical", ChatFormatting.LIGHT_PURPLE),
        DIVINE("Divine", ChatFormatting.YELLOW),
        CELESTIAL("Celestial", ChatFormatting.AQUA),
        ETERNAL("Eternal", ChatFormatting.DARK_RED);

        private final String displayName;
        private final ChatFormatting color;

        FishRarity(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ChatFormatting getColor() {
            return color;
        }
    }
}
