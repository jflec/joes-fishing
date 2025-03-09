package com.bigchadguys.fishing.command;

import com.bigchadguys.fishing.capability.FishingStats;
import com.bigchadguys.fishing.capability.FishingStatsCapability;
import com.bigchadguys.fishing.data.FishingLeaderboard;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class FishingLevelCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fishinglevel")
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "target");
                                            int newLevel = IntegerArgumentType.getInteger(context, "level");

                                            FishingStats stats = FishingStatsCapability.get(player);
                                            int oldLevel = stats.getFishingLevel();
                                            stats.setFishingLevel(newLevel);
                                            FishingLeaderboard.get().updatePlayerLevel(player, newLevel);
                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("Set fishing level of "
                                                            + player.getName().getString() + " from " + oldLevel + " to " + newLevel),
                                                    true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
