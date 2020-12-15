package com.funguscow.crossbreed.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * A modified chest screen that can handle more than 6 rows
 */
public class ResizableChestScreen extends ChestScreen {

    protected static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    protected final int numRows;

    public ResizableChestScreen(ChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        numRows = container.getNumRows();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = this.minecraft;
        if(minecraft == null)
            return;
        minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, Math.min(3, this.numRows) * 18 + 18);
        for(int rowsLeft = numRows - 3; rowsLeft > 0; rowsLeft -= 3){
            this.blit(matrixStack, i, j + (this.numRows - rowsLeft) * 18 + 18, 0, 72, this.xSize, Math.min(3, rowsLeft) * 18);
        }
        this.blit(matrixStack, i, j + this.numRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
