package rip.ysm.gpu;

/**
 * 桩实现：GUI 背景模糊效果属于阶段 3 移植。
 * 方法体为空，不会产生任何视觉效果。
 */
public final class BlurStack {
    private BlurStack() {}
    public static void pushBlur(float x, float y, float w, float h, float f1, float f2, int color) {}
    public static void flush(net.minecraft.client.gui.GuiGraphics guiGraphics) {}
}
