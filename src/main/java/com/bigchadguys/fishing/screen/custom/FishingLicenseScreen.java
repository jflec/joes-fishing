package com.bigchadguys.fishing.screen.custom;

import com.bigchadguys.fishing.JoesFishing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FishingLicenseScreen extends AbstractContainerScreen<FishingLicenseMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JoesFishing.MOD_ID, "textures/gui/fishing_license/fishing_license_gui.png");

    private static final int TAB_WIDTH = 24; // ✅ Width of each tab button
    private static final int TAB_HEIGHT = 18; // ✅ Height of each tab button

    private int selectedTab = 0; // ✅ Keeps track of the selected tab
    private Button statsTab, catchesTab, leaderboardTab;

    public FishingLicenseScreen(FishingLicenseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 174; // ✅ GUI width
        this.imageHeight = 106; // ✅ GUI height
    }

    @Override
    protected void init() {
        super.init();
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        this.statsTab = addRenderableWidget(Button.builder(Component.literal("Stats"), button -> setSelectedTab(0))
                .bounds(guiX + 69, guiY + 7, TAB_WIDTH, TAB_HEIGHT)
                .build());

        this.catchesTab = addRenderableWidget(Button.builder(Component.literal("Catches"), button -> setSelectedTab(1))
                .bounds(guiX + 103, guiY + 7, TAB_WIDTH, TAB_HEIGHT)
                .build());

        this.leaderboardTab = addRenderableWidget(Button.builder(Component.literal("Leaderboard"), button -> setSelectedTab(2))
                .bounds(guiX + 137, guiY + 7, TAB_WIDTH, TAB_HEIGHT)
                .build());
    }

    private void setSelectedTab(int tab) {
        this.selectedTab = tab;
    }

    private void renderTabButtons(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = 69;
        int buttonY = 7;
        int buttonWidth = 24;
        int buttonHeight = 18;

        // ✅ Button texture locations in the atlas
        int textureX = 0; // X position in the atlas
        int textureY = 106; // Y position in the atlas
        int hoverOffset = 18; // ✅ Moves the Y position when hovering

        // ✅ Render each button
        for (int i = 0; i < 3; i++) {
            int tabX = guiX + buttonX + (i * 34);
            boolean isHovered = mouseX >= tabX && mouseX < tabX + buttonWidth && mouseY >= guiY + buttonY && mouseY < guiY + buttonY + buttonHeight;

            int drawY = textureY + (selectedTab == i ? hoverOffset : 0); // ✅ Highlight selected tab
            if (isHovered) drawY += hoverOffset; // ✅ Apply hover effect

            guiGraphics.blit(GUI_TEXTURE, tabX, guiY + buttonY, textureX, drawY, buttonWidth, buttonHeight);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, guiX, guiY, 0, 0, imageWidth, imageHeight);

        // ✅ Render the tabs as part of the GUI
        renderTabButtons(guiGraphics, guiX, guiY, mouseX, mouseY);

        // ✅ Render the correct tab content
        renderTabContent(guiGraphics, guiX, guiY);
    }


    private void renderTabContent(GuiGraphics guiGraphics, int guiX, int guiY) {
        switch (selectedTab) {
            case 0 -> renderStatsTab(guiGraphics, guiX, guiY);
            case 1 -> renderRecentCatchesTab(guiGraphics, guiX, guiY);
            case 2 -> renderLeaderboardTab(guiGraphics, guiX, guiY);
        }
    }

    private void renderStatsTab(GuiGraphics guiGraphics, int guiX, int guiY) {
        guiGraphics.drawString(font, "Fishing Level: " + menu.getFishingLevel(), guiX + 100, guiY + 40, 0xFFFFFF, false);
        guiGraphics.drawString(font, "XP: " + menu.getFishingXp(), guiX + 100, guiY + 50, 0xFFFFFF, false);
        guiGraphics.drawString(font, "Total Fish Caught: " + menu.getTotalFishCaught(), guiX + 100, guiY + 60, 0xFFFFFF, false);
    }

    private void renderRecentCatchesTab(GuiGraphics guiGraphics, int guiX, int guiY) {
        String[] lastCatches = menu.getLast20Catches();
        for (int i = 0; i < lastCatches.length; i++) {
            guiGraphics.drawString(font, (i + 1) + ". " + lastCatches[i], guiX + 100, guiY + 40 + (i * 10), 0xFFFFFF, false);
        }
    }

    private void renderLeaderboardTab(GuiGraphics guiGraphics, int guiX, int guiY) {
        String[] leaderboard = menu.getLeaderboard();
        for (int i = 0; i < leaderboard.length; i++) {
            guiGraphics.drawString(font, (i + 1) + ". " + leaderboard[i], guiX + 100, guiY + 40 + (i * 10), 0xFFFFFF, false);
        }
    }
}
