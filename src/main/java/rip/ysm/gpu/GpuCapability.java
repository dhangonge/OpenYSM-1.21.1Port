package rip.ysm.gpu;

/**
 * 桩实现：GPU 加速渲染路径属于阶段 3 移植。
 * 当前返回 false，所有 GUI 模糊效果禁用，走纯色背景降级。
 */
public final class GpuCapability {
    private GpuCapability() {}
    public static boolean isAvailable() { return false; }
}
