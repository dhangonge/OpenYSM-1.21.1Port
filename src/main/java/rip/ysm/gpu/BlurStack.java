package rip.ysm.gpu;

import net.minecraft.client.gui.GuiGraphics;

/**
 * 桩实现：GUI 背景模糊效果属于阶段 3 移植（GPU 渲染路径）。
 * 方法体为空，不产生任何视觉效果。
 */
public final class BlurStack {
    private BlurStack() {}

    public static void pushBlur(float x, float y, float w, float h, float cornerRadius, float blurRadius) {}
    public static void pushBlur(float x, float y, float w, float h, float cornerRadius, float blurRadius, int tintRgba) {}
    public static void pushBlurPie(float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, float blurRadius) {}
    public static void pushBlurPie(float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, float blurRadius, int tintRgba) {}
    public static void popBlur() {}
    public static void clear() {}
    public static boolean isEmpty() { return true; }
    public static void flush(GuiGraphics graphics) {}
}
