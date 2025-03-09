package com.bigchadguys.fishing.capability;

import com.bigchadguys.fishing.JoesFishing;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FishingStatsCapability {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JoesFishing.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FishingStats>> FISHING_STATS =
            ATTACHMENTS.register("fishing_stats",
                    () -> AttachmentType.serializable(FishingStats::new).build());

    private static int clientFishingLevel = 1; // ✅ Default client level

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }

    public static FishingStats get(Player player) {
        return player.getData(FISHING_STATS.get());
    }

    public static int getClientFishingLevel() {
        return clientFishingLevel;
    }

    public static void setClientFishingLevel(int level) {
        clientFishingLevel = level;
    }
}
