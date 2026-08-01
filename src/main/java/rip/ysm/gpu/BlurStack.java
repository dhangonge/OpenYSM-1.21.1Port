package rip.ysm.gpu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

public final class BlurStack {
    private static final List<Region> regions = new ArrayList<>();
    private static final Matrix4f mvpScratch = new Matrix4f();
    private static final float[] mvpFloats = new float[16];
    private static long frameCounter = 0L;

    private BlurStack() {
    }

    public static void pushBlur(float x, float y, float w, float h, float cornerRadius, float blurRadius) {
        pushBlur(x, y, w, h, cornerRadius, blurRadius, 0xFFFFFFFF);
    }

    public static void pushBlur(float x, float y, float w, float h, float cornerRadius, float blurRadius, int tintRgba) {
        Region r = new Region();
        r.isPie = false;
        r.x = x;
        r.y = y;
        r.w = w;
        r.h = h;
        r.cornerRadius = cornerRadius;
        r.blurRadius = blurRadius;
        r.tintRgba = tintRgba;
        regions.add(r);
    }

    public static void pushBlurPie(float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, float blurRadius) {
        pushBlurPie(centerX, centerY, innerRadius, outerRadius, startAngle, endAngle, blurRadius, 0xFFFFFFFF);
    }

    public static void pushBlurPie(float centerX, float centerY, float innerRadius, float outerRadius, float startAngle, float endAngle, float blurRadius, int tintRgba) {
        float pad = 1.0f;
        Region r = new Region();
        r.isPie = true;
        r.x = centerX - outerRadius - pad;
        r.y = centerY - outerRadius - pad;
        r.w = (outerRadius + pad) * 2.0f;
        r.h = (outerRadius + pad) * 2.0f;
        r.pieCenterX = centerX;
        r.pieCenterY = centerY;
        r.pieInner = innerRadius;
        r.pieOuter = outerRadius;
        r.pieStart = startAngle;
        r.pieEnd = endAngle;
        r.blurRadius = blurRadius;
        r.tintRgba = tintRgba;
        regions.add(r);
    }

    public static void popBlur() {
        if (!regions.isEmpty()) regions.remove(regions.size() - 1);
    }

    /**
     * GLES/MobileGlues 上下文检测（双保险）：即使 isAndroid 判定失效（如 Zalith 不设置 MOD_ANDROID_RUNTIME），
     * 只要 GL 上下文是 GLES 或 MobileGlues 翻译层，就禁止执行桌面 GL 模糊着色器。
     */
    private static boolean isGlesContext() {
        try {
            String version = GL11.glGetString(GL11.GL_VERSION);
            if (version != null) {
                String v = version.toLowerCase();
                if (v.contains("es") || v.contains("mobileglues") || v.contains("gles")) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static void clear() {
        regions.clear();
    }

    public static boolean isEmpty() {
        return regions.isEmpty();
    }

    public static void flush(GuiGraphics graphics) {
        if (regions.isEmpty()) return;
        // 诊断：确认安卓判定与短路是否生效（MobileGlues/Zalith 环境下 isAndroid 可能为 false）
        com.elfmcys.yesstevemodel.YesSteveModel.LOGGER.info("[YSM] BlurStack.flush: regions={}, isAndroid={}, BLUR_GUI={}, gles={}",
                regions.size(), com.elfmcys.yesstevemodel.NativeLibLoader.isOnAndroid(),
                com.elfmcys.yesstevemodel.config.GeneralConfig.BLUR_GUI != null ? com.elfmcys.yesstevemodel.config.GeneralConfig.BLUR_GUI.get() : null,
                isGlesContext());
        // 上游（1.20.1）无安卓短路：用户实机对照证实桌面 GL 模糊在 MobileGlues 上正常（面板模糊+背景清晰），
        // 恢复执行以对齐上游行为。诊断日志保留，确认执行路径。
        if (!BlurShader.ensureCompiled()) {
            regions.clear();
            return;
        }

        frameCounter++;
        BlurShader.captureScreen(frameCounter);

        RenderSystem.getProjectionMatrix().mul(RenderSystem.getModelViewMatrix(), mvpScratch);
        mvpScratch.mul(graphics.pose().last().pose());
        mvpScratch.get(mvpFloats);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        GlStateManager._activeTexture(GL13.GL_TEXTURE0);
        GlStateManager._bindTexture(BlurShader.captureTextureId());
        GlStateManager._glUseProgram(BlurShader.program());

        if (BlurShader.locProj() >= 0) GL20.glUniformMatrix4fv(BlurShader.locProj(), false, mvpFloats);
        if (BlurShader.locScreenSize() >= 0)
            GL20.glUniform2f(BlurShader.locScreenSize(), BlurShader.captureWidth(), BlurShader.captureHeight());
        if (BlurShader.locGamma() >= 0) GL20.glUniform1f(BlurShader.locGamma(), 6.0f);

        GlStateManager._glBindVertexArray(BlurShader.dummyVao());

        for (Region r : regions) {
            float tr = ((r.tintRgba >> 16) & 0xFF) / 255.0f;
            float tg = ((r.tintRgba >> 8) & 0xFF) / 255.0f;
            float tb = (r.tintRgba & 0xFF) / 255.0f;
            float ta = ((r.tintRgba >> 24) & 0xFF) / 255.0f;
            if (BlurShader.locRect() >= 0) GL20.glUniform4f(BlurShader.locRect(), r.x, r.y, r.w, r.h);
            if (BlurShader.locRectSize() >= 0) GL20.glUniform2f(BlurShader.locRectSize(), r.w, r.h);
            if (BlurShader.locBlurRadius() >= 0)
                GL20.glUniform1f(BlurShader.locBlurRadius(), Math.max(1.0f, r.blurRadius));
            if (BlurShader.locTint() >= 0) GL20.glUniform4f(BlurShader.locTint(), tr, tg, tb, ta);
            if (r.isPie) {
                if (BlurShader.locMode() >= 0) GL20.glUniform1i(BlurShader.locMode(), 1);
                if (BlurShader.locPieCenter() >= 0)
                    GL20.glUniform2f(BlurShader.locPieCenter(), r.pieCenterX, r.pieCenterY);
                if (BlurShader.locPieInner() >= 0) GL20.glUniform1f(BlurShader.locPieInner(), r.pieInner);
                if (BlurShader.locPieOuter() >= 0) GL20.glUniform1f(BlurShader.locPieOuter(), r.pieOuter);
                if (BlurShader.locPieStart() >= 0) GL20.glUniform1f(BlurShader.locPieStart(), r.pieStart);
                if (BlurShader.locPieEnd() >= 0) GL20.glUniform1f(BlurShader.locPieEnd(), r.pieEnd);
                if (BlurShader.locPieFeather() >= 0) GL20.glUniform1f(BlurShader.locPieFeather(), 1.0f);
            } else {
                if (BlurShader.locMode() >= 0) GL20.glUniform1i(BlurShader.locMode(), 0);
                if (BlurShader.locRadius() >= 0) GL20.glUniform1f(BlurShader.locRadius(), r.cornerRadius);
                if (BlurShader.locCorner() >= 0) GL20.glUniform4f(BlurShader.locCorner(), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        GlStateManager._glUseProgram(0);
        BufferUploader.invalidate();
        GlStateManager._glBindVertexArray(0);
        RenderSystem.disableBlend();

        regions.clear();
    }

    private static final class Region {
        boolean isPie;
        float x;
        float y;
        float w;
        float h;
        float cornerRadius;
        float pieCenterX;
        float pieCenterY;
        float pieInner;
        float pieOuter;
        float pieStart;
        float pieEnd;
        float blurRadius;
        int tintRgba;
    }
}
