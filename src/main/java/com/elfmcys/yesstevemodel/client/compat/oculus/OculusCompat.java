package com.elfmcys.yesstevemodel.client.compat.oculus;

/**
 * STUB: libs/ 未提供 neoculus/oculus jar。
 * PBR 渲染支持不可用，所有状态恒为 false。
 */
public class OculusCompat {

    public static void init() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static boolean isPBRActive() {
        return false;
    }

    public static void updatePBRState() {
    }
}
