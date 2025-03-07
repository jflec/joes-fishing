package com.bigchadguys.fishing.block.entity;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.block.ModBlocks;
import com.bigchadguys.fishing.block.entity.custom.FishTrapBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JoesFishing.MOD_ID);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final Supplier<BlockEntityType<FishTrapBlockEntity>> FISH_TRAP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("fish_trap_block_entity", () -> BlockEntityType.Builder.of(
                    FishTrapBlockEntity::new, ModBlocks.FISH_TRAP.get()).build(null));
}
