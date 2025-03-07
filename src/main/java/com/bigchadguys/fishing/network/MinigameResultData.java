package com.bigchadguys.fishing.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MinigameResultData(int bobberId, boolean success) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MinigameResultData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("fishing", "minigame_result"));

    public static final StreamCodec<ByteBuf, MinigameResultData> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, MinigameResultData::bobberId,
                    ByteBufCodecs.BOOL, MinigameResultData::success,
                    MinigameResultData::new
            );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
