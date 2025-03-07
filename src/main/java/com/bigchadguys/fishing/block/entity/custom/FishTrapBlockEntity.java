package com.bigchadguys.fishing.block.entity.custom;

import com.bigchadguys.fishing.block.entity.ModBlockEntities;
import com.bigchadguys.fishing.item.ModItems;
import com.bigchadguys.fishing.screen.custom.FishTrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FishTrapBlockEntity extends BlockEntity implements MenuProvider {

    private static final int BAIT_SLOT = 0;
    private static final int FIRST_OUTPUT_SLOT = 1;
    private static final int SECOND_OUTPUT_SLOT = 2;
    private static final int THIRD_OUTPUT_SLOT = 3;
    private static final int FOURTH_OUTPUT_SLOT = 4;
    private static final int FIFTH_OUTPUT_SLOT = 5;
    private static final int SIXTH_OUTPUT_SLOT = 6;
    private static final int SEVENTH_OUTPUT_SLOT = 7;
    private static final int EIGHTH_OUTPUT_SLOT = 8;
    private static final int NINTH_OUTPUT_SLOT = 9;

    public final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public FishTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FISH_TRAP_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = createContainerData();
    }

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 1 -> maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("blockentity.fishing.fish_trap");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new FishTrapMenu(containerId, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (!pState.getValue(BlockStateProperties.WATERLOGGED)) {
            return;
        }

        ItemStack baitStack = this.itemHandler.getStackInSlot(BAIT_SLOT);

        if (!baitStack.isEmpty() && (baitStack.is(ModItems.WORM) || baitStack.is(ModItems.GUMMY_WORM)) && Math.random() < 0.1) {
            LootTable lootTable;

            if (baitStack.is(ModItems.GUMMY_WORM)) {
                lootTable = serverLevel.getServer()
                        .reloadableRegistries().getLootTable(BuiltInLootTables.FISHING_TREASURE);
            } else {
                lootTable = serverLevel.getServer()
                        .reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
            }

            LootParams.Builder contextBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pPos))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

            List<ItemStack> loot = lootTable.getRandomItems(contextBuilder.create(LootContextParamSets.FISHING));

            for (ItemStack itemStack : loot) {
                insertIntoInventory(itemStack);
            }

            this.itemHandler.extractItem(BAIT_SLOT, 1, false);
        }
    }

    private void insertIntoInventory(ItemStack stack) {
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            ItemStack remaining = this.itemHandler.insertItem(i, stack, false);
            if (remaining.isEmpty()) break;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
