package rip.ysm.compat.oculus;

/**
 * 上游 rip.ysm.compat.oculus 门面（原为 @ExpectPlatform 桩），
 * 转发到移植版的 com.elfmcys.yesstevemodel.client.compat.oculus.OculusCompat。
 * 当前 libs/ 未提供 neoculus jar，isLoaded() 恒 false。
 */
public final class OculusCompat {

    private OculusCompat() {
    }

    public static boolean isLoaded() {
        return com.elfmcys.yesstevemodel.client.compat.oculus.OculusCompat.isLoaded();
    }

    public static boolean isPBRActive() {
        return com.elfmcys.yesstevemodel.client.compat.oculus.OculusCompat.isPBRActive();
    }

    public static void updatePBRState() {
        com.elfmcys.yesstevemodel.client.compat.oculus.OculusCompat.updatePBRState();
    }

    public static boolean isShaderPackInUse() {
        return false;
    }

    public static boolean isRenderingShadowPass() {
        return false;
    }
}
