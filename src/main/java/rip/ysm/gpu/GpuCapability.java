package rip.ysm.gpu;

import com.elfmcys.yesstevemodel.config.GeneralConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

/**
 * GPU 能力检测。对齐 Sparkle-Morpher 的 Android/MobileGlues 方案：
 * - ForceGpuRenderer 配置（默认 true）开启 forced 模式：跳过扩展广告检查，
 *   MobileGlues/GLES 翻译层（报告桌面 GL 4.0 但扩展位缺失）下仍可用 GPU 渲染
 *   （layout(binding=0) 的 SSBO 在翻译层上正常工作，Sparkle 已验证）。
 * - 移除 isOnAndroid/GLES 硬拦截：GPU 能力由运行时检测 + forced 决定，不再按平台一刀切。
 */
public final class GpuCapability {
    private static volatile boolean checked = false;
    private static volatile boolean available = false;
    private static volatile boolean forced = false;
    private static volatile String reason = null;
    private static volatile boolean unavailableLogged;

    public static boolean isAvailable() {
        if (!checked) check();
        return available;
    }

    public static boolean isForced() {
        if (!checked) check();
        return forced;
    }

    public static String getReason() {
        if (!checked) check();
        return reason;
    }

    public static void logUnavailableOnce() {
        if (unavailableLogged || isAvailable()) {
            return;
        }
        unavailableLogged = true;
        System.out.println("[ysm] GPU unavailable: " + getReason());
    }

    public static synchronized void resetForTesting() {
        checked = false;
        available = false;
        forced = false;
        reason = null;
        unavailableLogged = false;
    }

    public static synchronized void check() {
        if (checked) return;
        checked = true;

        if (System.getProperty("OYSM_DISABLE_GPU") != null) {
            reason = "gpu renderer has been disabled (OYSM_DISABLE_GPU)";
            return;
        }
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            if (!isForceRequested()) {
                reason = "macOS GL is capped at 4.1 and lacks GL_ARB_shader_storage_buffer_object";
                return;
            }
        }

        GLCapabilities caps;
        String glVersion;
        String glRenderer;
        String glVendor;
        String glslVersion;
        try {
            RenderSystem.assertOnRenderThreadOrInit();
            caps = GL.getCapabilities();
            glVersion = GL11.glGetString(GL11.GL_VERSION);
            glRenderer = GL11.glGetString(GL11.GL_RENDERER);
            glVendor = GL11.glGetString(GL11.GL_VENDOR);
            glslVersion = GL11.glGetString(0x8B8C);
        } catch (Throwable t) {
            reason = "GL capabilities not available: " + t.getMessage();
            return;
        }

        if (glVersion == null) {
            reason = "GL version not available";
            return;
        }

        System.out.println("OpenGL version: " + glVersion);
        System.out.println("OpenGL renderer version: " + glRenderer);
        System.out.println("OpenGL vendor: " + glVendor);
        System.out.println("OpenGL glsl version: " + glslVersion);

        if (!caps.OpenGL30) {
            reason = "OpenGL 3.0 not supported (got " + glVersion + ")";
            return;
        }

        boolean force = isForceRequested();
        if (force) {
            // MobileGlues / under-reporting layers: skip extension ads; layout(binding=0) SSBO still works.
            available = true;
            forced = true;
            reason = "forced ok (skip GL extension ad check; GL " + glVersion + ", " + glRenderer + ")";
            System.out.println("[ysm] GPU capability forced: " + reason);
            return;
        }

        boolean hasSsbo = caps.OpenGL43 || caps.GL_ARB_shader_storage_buffer_object;
        boolean hasIfaceQuery = caps.OpenGL43 || caps.GL_ARB_program_interface_query;
        boolean hasLayoutBinding = caps.OpenGL42 || caps.GL_ARB_shading_language_420pack;
        boolean hasExplicitAttrib = caps.OpenGL33 || caps.GL_ARB_explicit_attrib_location;
        boolean hasPackedNormal = caps.OpenGL33 || caps.GL_ARB_vertex_type_2_10_10_10_rev;
        if (!hasSsbo) {
            reason = "SSBO not supported, GL_VERSION=" + glVersion;
            return;
        }
        if (!hasIfaceQuery) {
            reason = "GL_ARB_program_interface_query not supported; GL_VERSION=" + glVersion;
            return;
        }
        if (!hasLayoutBinding) {
            reason = "GL_ARB_shading_language_420pack not supported; GL_VERSION=" + glVersion;
            return;
        }
        if (!hasExplicitAttrib) {
            reason = "GL_ARB_explicit_attrib_location not supported; GL_VERSION=" + glVersion;
            return;
        }
        if (!hasPackedNormal) {
            reason = "GL_ARB_vertex_type_2_10_10_10_rev not supported; GL_VERSION=" + glVersion;
            return;
        }

        available = true;
        reason = "ok (GL " + glVersion + ", " + glRenderer + ")";
    }

    private static boolean isForceRequested() {
        String prop = System.getProperty("OYSM_FORCE_GPU");
        if (prop != null) {
            return !"false".equalsIgnoreCase(prop) && !"0".equals(prop);
        }
        return GeneralConfig.FORCE_GPU_RENDERER != null && GeneralConfig.FORCE_GPU_RENDERER.get();
    }
}
