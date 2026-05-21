package rip.ysm.api.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public interface HudOverlay extends LayeredDraw.Layer{
    void render(GuiGraphics guiGraphics, Font font, float partialTick, int screenWidth, int screenHeight);
}
