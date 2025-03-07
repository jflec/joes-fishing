package com.bigchadguys.fishing.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientRodTierDataHandler {
    public static int rodTier = 1;

    public static void handleDataOnMain(RodTierData data, IPayloadContext context) {
        context.enqueueWork(() -> rodTier = data.rodTier());
    }
}
