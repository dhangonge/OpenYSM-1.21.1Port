package com.elfmcys.yesstevemodel.mixin.client;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.1 原版引入 GUI 背景模糊（Screen.renderBlurredBackground → GameRenderer.processBlurEffect，
 * 受视频设置"菜单背景模糊度"控制）。在 MobileGlues（安卓 GLES→桌面 GL 翻译层）上该 PostChain
 * 模糊执行异常，表现为全屏背景模糊 + 与 YSM 面板模糊（BlurStack）叠加。
 * 对 YSM 自己的 Screen 禁用原版模糊：renderBlurredBackground 为空操作，
 * 保留 vanilla renderMenuBackground 的半透明背景（不模糊）。
 */
@Mixin(Screen.class)
public abstract class ScreenBlurDisableMixin {

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true, remap = false)
    private void ysm$disableVanillaScreenBlur(float partialTick, CallbackInfo ci) {
        Class<?> self = getClass();
        String name = self.getName();
        if (name.startsWith("com.elfmcys.yesstevemodel") || name.startsWith("rip.ysm")) {
            ci.cancel();
        }
    }
}
