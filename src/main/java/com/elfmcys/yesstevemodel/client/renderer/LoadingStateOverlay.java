package com.elfmcys.yesstevemodel.client.renderer;

import com.elfmcys.yesstevemodel.client.gui.ExtraPlayerRenderScreen;
import com.elfmcys.yesstevemodel.config.ExtraPlayerRenderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;

public class LoadingStateOverlay implements LayeredDraw.Layer {
    public void render(GuiGraphics guiGraphics, float partialTick) {
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        Minecraft minecraft;
        LocalPlayer localPlayer;
        if (ExtraPlayerRenderConfig.DISABLE_PLAYER_RENDER.get() || (localPlayer = (minecraft = Minecraft.getInstance()).player) == null || (minecraft.screen instanceof ExtraPlayerRenderScreen)) {
            return;
        }
        ModelPreviewRenderer.renderPlayerOverlay(guiGraphics, localPlayer, ExtraPlayerRenderConfig.PLAYER_POS_X.get(), ExtraPlayerRenderConfig.PLAYER_POS_Y.get(), ExtraPlayerRenderConfig.PLAYER_SCALE.get().floatValue(), ExtraPlayerRenderConfig.PLAYER_YAW_OFFSET.get().floatValue(), -500, minecraft.getFrameTime());
    }
}