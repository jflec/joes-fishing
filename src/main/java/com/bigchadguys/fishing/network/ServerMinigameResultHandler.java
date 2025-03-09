package com.bigchadguys.fishing.network;

import com.bigchadguys.fishing.client.BobberTracker;
import com.bigchadguys.fishing.entity.custom.CustomFishingBobberEntity;
import com.bigchadguys.fishing.item.custom.FishingEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerMinigameResultHandler {
    public static void handleMinigameResult(MinigameResultData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            Entity entity = serverPlayer.level().getEntity(data.bobberId());
            if (entity instanceof CustomFishingBobberEntity bobber) {
                bobber.finishMinigame();
                if (data.success()) {
                    FishingEventHandler.awardLoot(serverPlayer);
                } else {
                    FishingEventHandler.awardJunk(serverPlayer);
                }
                BobberTracker.currentBobberId = -1;
            }
        });
    }
}
