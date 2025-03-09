package com.bigchadguys.fishing.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FishingStatsSyncPacket(int fishingLevel, int fishingXp, int totalFishCaught) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FishingStatsSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("fishing", "stats_sync"));

    public static final StreamCodec<ByteBuf, FishingStatsSyncPacket> CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, FishingStatsSyncPacket::fishingLevel,
                    ByteBufCodecs.VAR_INT, FishingStatsSyncPacket::fishingXp,
                    ByteBufCodecs.VAR_INT, FishingStatsSyncPacket::totalFishCaught,
                    FishingStatsSyncPacket::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
