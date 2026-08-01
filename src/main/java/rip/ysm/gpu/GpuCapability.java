package rip.ysm.gpu;

import com.elfmcys.yesstevemodel.NativeLibLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

public final class GpuCapability {
    private static volatile boolean checked = false;
    private static volatile boolean available = false;
    private static volatile String reason = null;

    public static boolean isAvailable() {
        if (!checked) check();
        return available;
    }

    public static String getReason() {
        if (!checked) check();
        return reason;
    }

    public static synchronized void check() {
        if (checked) return;
        checked = true;

        if (System.getProperty("OYSM_DISABLE_GPU") != null) {
            reason = "gpu renderer has been disabled";
            return;
        }
        if (!NativeLibLoader.isLoaded()) {
            reason = "native ysm-core not loaded";
            return;
        }
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            reason = "macOS GL is capped at 4.1 and lacks GL_ARB_shader_storage_buffer_object";
            return;
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

        // GLES/MobileGlues 环境（安卓翻译层）不视为桌面 OpenGL：即使报告 GL 4.0 Core Profile，
        // 也缺少桌面 GL 的完整特性（SSBO/程序接口查询等），且桌面 GL 着色器路径会破坏渲染状态
        if (glslVersion != null && (glslVersion.contains("OpenGL ES") || glslVersion.toLowerCase().contains("mobileglues"))) {
            reason = "GLES context detected (" + glslVersion + ")";
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
}
