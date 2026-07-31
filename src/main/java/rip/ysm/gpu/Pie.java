package rip.ysm.gpu;

import net.minecraft.client.gui.GuiGraphics;

/**
 * 桩实现：饼图渲染属于阶段 3 移植（GPU 渲染路径）。
 * 方法体为空，不产生视觉效果。
 */
public final class Pie {
    private Pie() {}
    public static final float tau = (float) (Math.PI * 2.0);
    public static void draw(GuiGraphics graphics, float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, int rgba) {}
    public static void draw(GuiGraphics graphics, float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, int rgba, float feather) {}
}
