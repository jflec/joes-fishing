package com.bigchadguys.fishing.datagen;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JoesFishing.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WORM.get());
        basicItem(ModItems.GUMMY_WORM.get());
        basicItem(ModItems.TREASURE_CHEST.get());
        basicItem(ModItems.HYDRITE_TREASURE_CHEST.get());

        basicItem(ModItems.INTERMEDIATE_MATERIAL.get());
        basicItem(ModItems.FISHING_LICENSE.get());

    }
}
