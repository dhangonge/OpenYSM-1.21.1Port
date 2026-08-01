package rip.ysm.compat.optifine;

/**
 * 上游 rip.ysm.compat.optifine 门面（原为 @ExpectPlatform 桩）。
 * OptiFine 在 NeoForge 1.21.1 不可用，恒返回 false。
 */
public final class OptiFineDetector {

    private OptiFineDetector() {
    }

    public static boolean isOptifinePresent() {
        return false;
    }
}
