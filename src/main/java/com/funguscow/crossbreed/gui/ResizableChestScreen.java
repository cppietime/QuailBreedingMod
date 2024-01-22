package com.funguscow.crossbreed.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;

/**
 * A modified chest screen that can handle more than 6 rows
 */
public class ResizableChestScreen extends AbstractContainerScreen<ChestMenu> {

    protected static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    protected final int numRows;

    public ResizableChestScreen(ChestMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        numRows = container.getRowCount();
        this.imageHeight = 114 + numRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float partialTicks) {
        renderBackground(guiGraphics, pMouseX, pMouseY, partialTicks);
        super.render(guiGraphics, pMouseX, pMouseY, partialTicks);
        renderTooltip(guiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null)
            return;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CHEST_GUI_TEXTURE, i, j, 0, 0, this.getXSize(), Math.min(3, this.numRows) * 18 + 18);
        for (int rowsLeft = numRows - 3; rowsLeft > 0; rowsLeft -= 3) {
            guiGraphics.blit(CHEST_GUI_TEXTURE, i, j + (this.numRows - rowsLeft) * 18 + 18, 0, 72, this.imageWidth, Math.min(3, rowsLeft) * 18);
        }
        guiGraphics.blit(CHEST_GUI_TEXTURE, i, j + this.numRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
