package com.bigchadguys.fishing.item.custom;

import com.bigchadguys.fishing.JoesFishing;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.Set;

import static com.bigchadguys.fishing.data.ModDataComponents.FISH_RARITY;

@EventBusSubscriber(modid = JoesFishing.MOD_ID)
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

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        NonNullList<ItemStack> drops = event.getDrops();
        Player player = event.getEntity();
        Level level = player.level();
        ItemStack fishingRod = player.getMainHandItem();

        if (!(fishingRod.getItem() instanceof CustomFishingRodItem)) {
            return;
        }

        boolean fishCaught = false;

        for (ItemStack itemStack : drops) {
            if (isFish(itemStack)) {
                fishCaught = true;
                FishRarity rarity = determineFishRarity();
                applyFishRarityToItem(itemStack, rarity);

                String article = startsWithVowel(rarity.getDisplayName()) ? "an" : "a";
                String fishName = rarity.getDisplayName() + " " + getFishName(itemStack);
                Component message = Component.literal("You caught " + article + " " + fishName + "!")
                        .withStyle(rarity.getColor());

                if (!level.isClientSide && BROADCAST_RARITIES.contains(rarity)) {
                    broadcastMessage(level, player, fishName, rarity);
                    playSuccessEffects(level, player);
                } else {
                    player.sendSystemMessage(message);
                }
            }
        }


    }

    private static boolean isFish(ItemStack item) {
        return item.is(Items.COD) || item.is(Items.SALMON) || item.is(Items.TROPICAL_FISH) || item.is(Items.PUFFERFISH);
    }

    private static FishRarity determineFishRarity() {
        FishRarity[] rarities = FishRarity.values();
        return rarities[RANDOM.nextInt(rarities.length)];

        /*
        // Original rarity percentages:
        float roll = RANDOM.nextFloat();
        if (roll < 0.55) return FishRarity.COMMON;
        if (roll < 0.85) return FishRarity.UNCOMMON;
        if (roll < 0.975) return FishRarity.RARE;
        if (roll < 0.9975) return FishRarity.EPIC;
        if (roll < 0.9996) return FishRarity.EXOTIC;
        if (roll < 0.99984) return FishRarity.LEGENDARY;
        if (roll < 0.99992) return FishRarity.TRANSCENDENT;
        if (roll < 0.99996) return FishRarity.MYTHICAL;
        if (roll < 0.99999) return FishRarity.DIVINE;
        if (roll < 0.999996) return FishRarity.CELESTIAL;
        return FishRarity.ETERNAL;
        */
    }

    private static void applyFishRarityToItem(ItemStack item, FishRarity rarity) {
        item.set(FISH_RARITY.get(), rarity.name());

        Component displayName = Component.literal(rarity.getDisplayName() + " " + getFishName(item))
                .withStyle(style -> style.withColor(rarity.getColor()).withItalic(false));

        item.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, displayName);
    }

    private static String getFishName(ItemStack item) {
        if (item.is(Items.COD)) return "Cod";
        if (item.is(Items.SALMON)) return "Salmon";
        if (item.is(Items.TROPICAL_FISH)) return "Tropical Fish";
        if (item.is(Items.PUFFERFISH)) return "Pufferfish";
        return "Fish";
    }

    private static boolean startsWithVowel(String word) {
        return word.matches("(?i)^[AEIOU].*");
    }

    private static void broadcastMessage(Level level, Player player, String fishName, FishRarity rarity) {
        MinecraftServer server = ((ServerLevel) level).getServer();
        String odds = FISH_RARITY_ODDS.getOrDefault(rarity, "Unknown Odds");
        Component broadcastMessage = Component.literal(player.getName().getString() + " caught a " + fishName + "! (" + odds + ")")
                .withStyle(rarity.getColor());

        server.getPlayerList().broadcastSystemMessage(broadcastMessage, false);
    }

    private static void playSuccessEffects(Level level, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, player.getSoundSource(), 1.0F, 1.0F);
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

    public static void awardLoot(Player player) {
        System.out.println("you won!");

    }

    public static void awardJunk(Player player) {
        System.out.println("you lost!");
    }
}
