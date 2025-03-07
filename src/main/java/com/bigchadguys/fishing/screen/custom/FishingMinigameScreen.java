package com.bigchadguys.fishing.screen.custom;

import com.bigchadguys.fishing.JoesFishing;
import com.bigchadguys.fishing.client.BobberTracker;
import com.bigchadguys.fishing.item.custom.FishingEventHandler;
import com.bigchadguys.fishing.network.MinigameResultData;
import com.bigchadguys.fishing.util.FishingMinigameLogic;
import com.bigchadguys.fishing.util.FishingMinigameUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FishingMinigameScreen extends AbstractContainerScreen<FishingMinigameMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(JoesFishing.MOD_ID, "textures/gui/fishing_minigame/fishing_minigame_gui.png");
    private static final String[] TARGET_TEXTURE_NAMES = {"blue_fish"};
    private final ResourceLocation targetResource;
    private static final int ATLAS_WIDTH = 256;
    private static final int ATLAS_HEIGHT = 256;
    private static final int IMAGE_WIDTH = 196;
    private static final int IMAGE_HEIGHT = 93;
    private static final int BUTTON_WIDTH = 40;
    private static final int BUTTON_HEIGHT = 25;
    private static final int BUTTON_OFFSET_X = 78;
    private static final int BUTTON_OFFSET_Y = 61;
    private static final int PROGRESS_BAR_WIDTH = 180;
    private static final int PROGRESS_BAR_HEIGHT = 8;
    private static final int PROGRESS_BAR_OFFSET_X = 8;
    private static final int PROGRESS_BAR_OFFSET_Y = 48;
    private static final int PROGRESS_BAR_TEXTURE_X = 0;
    private static final int PROGRESS_BAR_TEXTURE_Y = 133;
    private static final int HOOK_WIDTH = 11;
    private static final int HOOK_HEIGHT = 31;
    private static final int HOOK_TRACK_OFFSET_Y = 8;
    private static final int FISH_HEIGHT = 34;
    private static final int WIGGLE_AMPLITUDE = 2;
    private static final int KELP1_TEXTURE_X = 0;
    private static final int KELP1_TEXTURE_Y = 216;
    private static final int KELP1_WIDTH = 8;
    private static final int KELP1_HEIGHT = 19;
    private static final int KELP2_TEXTURE_X = 8;
    private static final int KELP2_TEXTURE_Y = 216;
    private static final int KELP2_WIDTH = 9;
    private static final int KELP2_HEIGHT = 19;
    private static final int KELP1_MENU_OFFSET_X = 30;
    private static final int KELP1_MENU_OFFSET_Y = 23;
    private static final int KELP2_MENU_OFFSET_X = 156;
    private static final int KELP2_MENU_OFFSET_Y = 23;
    private static final int CHEST_WIDTH = 16;
    private static final int CHEST_HEIGHT = 16;
    private static final int VERTICAL_BOB_AMPLITUDE = 2;
    private static final ResourceLocation CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(JoesFishing.MOD_ID, "textures/item/treasure_chest.png");
    private static final ResourceLocation HYDRITE_CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(JoesFishing.MOD_ID, "textures/item/hydrite_treasure_chest.png");
    private final int fishAtlasWidth;
    private final int rodTier;
    private final int fishWidth;
    private final FishingMinigameLogic logic;
    private static final long NANOS_PER_TICK = 5_000_000;
    private long lastLogicUpdate = System.nanoTime();

    public FishingMinigameScreen(FishingMinigameMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.rodTier = menu.getRodTier();
        FishingEventHandler.FishRarity fishFishRarity = determineFishRarity();
        float difficultyMultiplier = FishingMinigameUtil.getDifficultyMultiplier(fishFishRarity);
        int fullWidth = FishingMinigameUtil.getHardcodedFishAtlasWidth(rodTier);
        this.fishWidth = fullWidth / 3;
        this.fishAtlasWidth = fullWidth;
        int index = new Random().nextInt(TARGET_TEXTURE_NAMES.length);
        String textureName = TARGET_TEXTURE_NAMES[index];
        String tierFolder = FishingMinigameUtil.getTierFolder(rodTier);
        targetResource = ResourceLocation.fromNamespaceAndPath(JoesFishing.MOD_ID,
                "textures/gui/fishing_minigame/" + tierFolder + "/" + textureName + ".png");
        inventoryLabelY = 10000;
        titleLabelY = 10000;
        logic = new FishingMinigameLogic(rodTier, fishWidth, difficultyMultiplier, true);
    }

    private void updateLogic() {
        long now = System.nanoTime();
        int ticksProcessed = 0;
        while (now - lastLogicUpdate >= NANOS_PER_TICK && ticksProcessed < 10) {
            logic.tick();
            lastLogicUpdate += NANOS_PER_TICK;
            ticksProcessed++;
        }
        if (logic.isGameOver()) {
            onClose();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        boolean success = logic.bonusProgress >= 1.0f;
        int bobberId = BobberTracker.currentBobberId;
        PacketDistributor.sendToServer(new MinigameResultData(bobberId, success));
    }

    private int getGuiX() {
        return (width - IMAGE_WIDTH) / 2;
    }

    private int getGuiY() {
        return (height - IMAGE_HEIGHT) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int guiX = getGuiX();
        int guiY = getGuiY();
        guiGraphics.blit(GUI_TEXTURE, guiX, guiY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        renderButton(guiGraphics, mouseX, mouseY);
        renderProgressBar(guiGraphics);
        renderHook(guiGraphics);
        renderTarget(guiGraphics, partialTick);
    }

    private void renderButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        int buttonX = guiX + BUTTON_OFFSET_X;
        int buttonY = guiY + BUTTON_OFFSET_Y;
        int textureY = 141;
        if (logic.isButtonHeld) {
            textureY += BUTTON_HEIGHT * 2;
        } else if (mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH && mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
            textureY += BUTTON_HEIGHT;
        }
        guiGraphics.blit(GUI_TEXTURE, buttonX, buttonY, 0, textureY, BUTTON_WIDTH, BUTTON_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private void renderProgressBar(GuiGraphics guiGraphics) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        int progressBarX = guiX + PROGRESS_BAR_OFFSET_X;
        int progressBarY = guiY + PROGRESS_BAR_OFFSET_Y;
        int filledWidth = (int) (PROGRESS_BAR_WIDTH * Math.min(logic.bonusProgress, 1.0f));
        if (filledWidth > 0) {
            guiGraphics.blit(GUI_TEXTURE, progressBarX, progressBarY, PROGRESS_BAR_TEXTURE_X, PROGRESS_BAR_TEXTURE_Y, filledWidth, PROGRESS_BAR_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        }
    }

    private void renderHook(GuiGraphics guiGraphics) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        int hookX = guiX + logic.getHookScreenX();
        int hookY = guiY + logic.getHookScreenY();
        guiGraphics.blit(GUI_TEXTURE, hookX, hookY, 196, 0, HOOK_WIDTH, HOOK_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private void renderTarget(GuiGraphics guiGraphics, float delta) {
        RenderSystem.setShaderTexture(0, targetResource);
        int guiX = getGuiX();
        int guiY = getGuiY();
        int targetX = guiX + logic.getCurrentTargetX();
        int targetYOffset = logic.getCurrentTargetYOffset();
        int targetY = guiY + HOOK_TRACK_OFFSET_Y + targetYOffset;
        int hookX = guiX + logic.getHookScreenX();
        int hookY = guiY + logic.getHookScreenY();
        boolean targetCollision = hookX < targetX + fishWidth && hookX + HOOK_WIDTH > targetX &&
                hookY < targetY + FISH_HEIGHT && hookY + HOOK_HEIGHT > targetY;
        if (targetCollision) {
            int horizontalWiggleOffset = (int) (Math.sin(delta * (Math.PI / 24)) * WIGGLE_AMPLITUDE);
            targetX += horizontalWiggleOffset;
            targetY += horizontalWiggleOffset;
        }
        int textureOffsetX = (logic.targetGoalPos - logic.targetStartPos >= 0) ? fishWidth : 0;
        guiGraphics.blit(targetResource, targetX, targetY, textureOffsetX, 0, fishWidth, FISH_HEIGHT, fishAtlasWidth, FISH_HEIGHT);
    }

    private void renderFrameImage(GuiGraphics guiGraphics) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        guiGraphics.blit(GUI_TEXTURE, guiX + 5, guiY + 5, 0, 93, 186, 40, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private void renderKelpImages(GuiGraphics guiGraphics) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        guiGraphics.blit(GUI_TEXTURE, guiX + KELP1_MENU_OFFSET_X, guiY + KELP1_MENU_OFFSET_Y, KELP1_TEXTURE_X, KELP1_TEXTURE_Y, KELP1_WIDTH, KELP1_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        guiGraphics.blit(GUI_TEXTURE, guiX + KELP2_MENU_OFFSET_X, guiY + KELP2_MENU_OFFSET_Y, KELP2_TEXTURE_X, KELP2_TEXTURE_Y, KELP2_WIDTH, KELP2_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private void renderTreasureChest(GuiGraphics guiGraphics, float delta) {
        if (!logic.treasureActive || logic.treasureClaimed) return;
        int guiX = getGuiX();
        int guiY = getGuiY();
        logic.chestVerticalAnimTime += delta * 0.1f;
        int verticalOffset = (int) (Math.sin(logic.chestVerticalAnimTime) * VERTICAL_BOB_AMPLITUDE);
        int trackX = guiX + PROGRESS_BAR_OFFSET_X;
        int rawRenderX = trackX + logic.treasureChestX;
        int horizontalWiggleOffset = 0;
        int hookX = guiX + logic.getHookScreenX();
        int hookY = guiY + logic.getHookScreenY();
        boolean collision = hookX < (trackX + logic.treasureChestX) + CHEST_WIDTH &&
                hookX + HOOK_WIDTH > (trackX + logic.treasureChestX) &&
                hookY < (guiY + logic.treasureChestY + verticalOffset) + CHEST_HEIGHT &&
                hookY + CHEST_HEIGHT > (guiY + logic.treasureChestY + verticalOffset);
        if (collision) {
            horizontalWiggleOffset = (int) (Math.sin(delta * (Math.PI / 16)) * WIGGLE_AMPLITUDE);
        }
        int renderX = Math.max(trackX, Math.min(rawRenderX + horizontalWiggleOffset, trackX + PROGRESS_BAR_WIDTH - CHEST_WIDTH));
        int renderY = guiY + logic.treasureChestY + verticalOffset;
        ResourceLocation chestTextureToUse = (rodTier == 5) ? HYDRITE_CHEST_TEXTURE : CHEST_TEXTURE;
        RenderSystem.setShaderTexture(0, chestTextureToUse);
        guiGraphics.blit(chestTextureToUse, renderX, renderY, 0, 0, CHEST_WIDTH, CHEST_HEIGHT, CHEST_WIDTH, CHEST_HEIGHT);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        updateLogic();
        renderBg(guiGraphics, delta, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderFrameImage(guiGraphics);
        renderKelpImages(guiGraphics);
        renderTreasureChest(guiGraphics, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiX = getGuiX();
        int guiY = getGuiY();
        if (mouseX >= guiX + BUTTON_OFFSET_X && mouseX <= guiX + BUTTON_OFFSET_X + BUTTON_WIDTH &&
                mouseY >= guiY + BUTTON_OFFSET_Y && mouseY <= guiY + BUTTON_OFFSET_Y + BUTTON_HEIGHT) {
            logic.isButtonHeld = true;
            if (!logic.hasActivatedHook) {
                logic.hasActivatedHook = true;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        logic.isButtonHeld = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private FishingEventHandler.FishRarity determineFishRarity() {
        FishingEventHandler.FishRarity[] rarities = FishingEventHandler.FishRarity.values();
        return rarities[new Random().nextInt(rarities.length)];
    }
}
