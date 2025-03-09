package com.bigchadguys.fishing;

import com.bigchadguys.fishing.block.ModBlocks;
import com.bigchadguys.fishing.block.entity.ModBlockEntities;
import com.bigchadguys.fishing.capability.FishingStatsCapability;
import com.bigchadguys.fishing.command.FishingLevelCommand;
import com.bigchadguys.fishing.data.ModDataComponents;
import com.bigchadguys.fishing.item.ModCreativeModeTabs;
import com.bigchadguys.fishing.item.ModItems;
import com.bigchadguys.fishing.network.*;
import com.bigchadguys.fishing.screen.ModMenuTypes;
import com.bigchadguys.fishing.screen.custom.FishTrapScreen;
import com.bigchadguys.fishing.screen.custom.FishingLicenseScreen;
import com.bigchadguys.fishing.screen.custom.FishingMinigameScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.bigchadguys.fishing.network.FishingStatsCapability.setClientStats;

@Mod(JoesFishing.MOD_ID)
public class JoesFishing {
    public static final String MOD_ID = "fishing";

    public JoesFishing(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        FishingStatsCapability.register(modEventBus);
    }

    @EventBusSubscriber(modid = JoesFishing.MOD_ID, bus = EventBusSubscriber.Bus.MOD) // ✅ Ensure it runs on the FORGE event bus
    public static class ServerModEvents {
        @SubscribeEvent
        public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");

            registrar.playBidirectional(
                    RodTierData.TYPE,
                    RodTierData.CODEC,
                    new DirectionalPayloadHandler<>(
                            ClientRodTierDataHandler::handleDataOnMain,
                            (data, context) -> {
                            }
                    )
            );
            registrar.playToServer(
                    MinigameResultData.TYPE,
                    MinigameResultData.CODEC,
                    ServerMinigameResultHandler::handleMinigameResult
            );

            registrar.playToClient(
                    FishingStatsSyncPacket.TYPE,
                    FishingStatsSyncPacket.CODEC,
                    (packet, context) -> {
                        context.enqueueWork(() -> setClientStats(
                                packet.fishingLevel(),
                                packet.fishingXp(),
                                packet.totalFishCaught()
                        ));
                    }
            );
        }
    }

    @EventBusSubscriber(modid = JoesFishing.MOD_ID)
    public static class CommandRegistry {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            FishingLevelCommand.register(event.getDispatcher()); // Register the fishinglevel command
        }
    }

    @EventBusSubscriber(modid = JoesFishing.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.FISH_TRAP_MENU.get(), FishTrapScreen::new);
            event.register(ModMenuTypes.FISHING_MINIGAME_MENU.get(), FishingMinigameScreen::new);
            event.register(ModMenuTypes.FISHING_LICENSE_MENU.get(), FishingLicenseScreen::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            for (Item rod : ModItems.getCustomFishingRods()) {
                ItemProperties.register(rod, ResourceLocation.fromNamespaceAndPath("minecraft", "cast"),
                        (stack, world, entity, seed) ->
                                (entity instanceof Player player && player.fishing != null &&
                                        (player.getItemInHand(InteractionHand.MAIN_HAND).equals(stack) ||
                                                player.getItemInHand(InteractionHand.OFF_HAND).equals(stack))) ? 1.0F : 0.0F
                );
            }
        }
    }
}
