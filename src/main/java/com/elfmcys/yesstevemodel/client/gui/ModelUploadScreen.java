package com.elfmcys.yesstevemodel.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 桩实现：模型上传功能依赖阶段 4 网络握手（isOysmServer 恒 false，上传按钮禁用）。
 * 完整实现见上游 ModelUploadScreen + ModelUploadSession + C2SModelUpload*Packet。
 */
public class ModelUploadScreen extends Screen {
    private final Screen parentScreen;

    public ModelUploadScreen(Screen parent) {
        super(Component.literal("upload"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {
        clearWidgets();
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                        Component.literal("Back"),
                        button -> Minecraft.getInstance().setScreen(this.parentScreen))
                .bounds(this.width - 70, 10, 60, 18)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("gui.yes_steve_model.upload_unavailable"),
                this.width / 2, this.height / 2 - 8, 0xFFFFFF);
    }
}
