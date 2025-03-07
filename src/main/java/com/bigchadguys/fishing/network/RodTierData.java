package com.bigchadguys.fishing.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RodTierData(int rodTier) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RodTierData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("fishing", "rod_tier"));

    public static final StreamCodec<ByteBuf, RodTierData> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    RodTierData::rodTier,
                    RodTierData::new
            );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
