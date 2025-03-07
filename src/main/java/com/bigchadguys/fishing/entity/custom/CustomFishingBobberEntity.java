package com.bigchadguys.fishing.entity.custom;

import com.bigchadguys.fishing.client.BobberTracker;
import com.bigchadguys.fishing.network.RodTierData;
import com.bigchadguys.fishing.screen.custom.FishingMinigameMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.ThreadLocalRandom;

public class CustomFishingBobberEntity extends FishingHook {
    private boolean minigameTriggered = false;
    private boolean minigameActive = false;
    private final int rodTier;
    private int ticksInWater = 0;
    private final int triggerTicks;

    public CustomFishingBobberEntity(Player player, Level level, int luck, int lureSpeed, int rodTier) {
        super(player, level, luck, lureSpeed);
        this.rodTier = rodTier;
        this.triggerTicks = ThreadLocalRandom.current().nextInt(40, 81);
    }

    private boolean isOpenWaterEnvironment() {
        Level lvl = level();
        BlockPos center = blockPosition();
        FluidState centerFluid = lvl.getFluidState(center);
        if (!centerFluid.is(FluidTags.WATER) || !centerFluid.isSource()) {
            return false;
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = lvl.getBlockState(pos);
                    if (dy == 0) {
                        FluidState fs = state.getFluidState();
                        if (!fs.is(FluidTags.WATER) || !fs.isSource() || !state.getCollisionShape(lvl, pos).isEmpty()) {
                            return false;
                        }
                    } else {
                        if (!(state.isAir() || state.is(Blocks.LILY_PAD) || state.is(Blocks.BUBBLE_COLUMN) ||
                                (state.getFluidState().is(FluidTags.WATER) && state.getFluidState().isSource() && state.getCollisionShape(lvl, pos).isEmpty()))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        Level lvl = level();
        if (!lvl.isClientSide()) {
            if (getId() != -1) {
                BobberTracker.currentBobberId = getId();
            }
            BlockPos pos = blockPosition();
            boolean inWater = lvl.getFluidState(pos).is(FluidTags.WATER);
            ticksInWater = inWater ? ticksInWater + 1 : 0;
            if (!minigameTriggered && ticksInWater >= triggerTicks && isOpenWaterEnvironment()) {
                Player owner = getPlayerOwner();
                if (owner instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer, new RodTierData(rodTier));
                    serverPlayer.openMenu(new FishingMinigameMenuProvider());
                    minigameTriggered = true;
                    minigameActive = true;
                }
            }
        }
    }

    public void finishMinigame() {
        BobberTracker.currentBobberId = -1;
        discard();
    }
}
