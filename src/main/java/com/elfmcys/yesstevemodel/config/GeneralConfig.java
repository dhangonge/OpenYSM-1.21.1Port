package com.elfmcys.yesstevemodel.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GeneralConfig {

    public static ModConfigSpec.BooleanValue DISCLAIMER_SHOW;

    public static ModConfigSpec.BooleanValue PRINT_ANIMATION_ROULETTE_MSG;

    public static ModConfigSpec.BooleanValue DISABLE_SELF_MODEL;

    public static ModConfigSpec.BooleanValue DISABLE_OTHER_MODEL;

    public static ModConfigSpec.BooleanValue DISABLE_SELF_HANDS;

    public static ModConfigSpec.BooleanValue DISABLE_PROJECTILE_MODEL;

    public static ModConfigSpec.BooleanValue DISABLE_VEHICLE_MODEL;

    public static ModConfigSpec.BooleanValue DISABLE_EXTERNAL_FP_ANIM;

    public static ModConfigSpec.BooleanValue USE_COMPATIBILITY_RENDERER;

    public static ModConfigSpec.DoubleValue SOUND_VOLUME;

    public static ModConfigSpec.BooleanValue SHOW_MODEL_ID_FIRST;

    public static ModConfigSpec.BooleanValue SOPHISTICATEDBACKPACK;

    public static ModConfigSpec.BooleanValue PARCOOL;

    public static ModConfigSpec.BooleanValue USE_GPU_RENDERER;
    /** 强制 GPU 渲染：MobileGlues/翻译层下跳过扩展检查（Sparkle-Morpher 验证在安卓 GLES 翻译层可用） */
    public static ModConfigSpec.BooleanValue FORCE_GPU_RENDERER;

    public static ModConfigSpec.BooleanValue LAZY_MODEL_LOADING;

    public static ModConfigSpec.BooleanValue FORCE_CLIENT_MODE;

    public static ModConfigSpec.DoubleValue HANDSHAKE_TIMEOUT;

    public static ModConfigSpec.DoubleValue SEARCH_SUGGESTION_COUNT;

    public static ModConfigSpec.EnumValue<RouletteSettingsMode> ROULETTE_SETTINGS_MODE;

    public static ModConfigSpec.EnumValue<RouletteMode> ROULETTE_MODE;

    public static ModConfigSpec.BooleanValue BLUR_GUI;

    public static ModConfigSpec.EnumValue<TextureScreenMode> TEXTURE_SCREEN_MODE;

    public static ModConfigSpec.EnumValue<ModelInfoScreenMode> MODEL_INFO_SCREEN_MODE;

    public enum RouletteSettingsMode {
        MODERN,
        CLASSIC
    }

    public enum RouletteMode {
        MODERN,
        CLASSIC
    }

    public enum TextureScreenMode {
        MODERN,
        CLASSIC
    }

    public enum ModelInfoScreenMode {
        MODERN,
        CLASSIC
    }

    public static boolean effectiveModernRoulette() {
        if (ROULETTE_MODE == null || ROULETTE_SETTINGS_MODE == null) return false;
        return ROULETTE_MODE.get() == RouletteMode.MODERN && ROULETTE_SETTINGS_MODE.get() == RouletteSettingsMode.MODERN;
    }

    public static ModConfigSpec buildSpec() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        defineGeneral(builder);
        ExtraPlayerRenderConfig.define(builder);
        LoadingStateConfig.define(builder);
        return builder.build();
    }

    public static void defineGeneral(ModConfigSpec.Builder builder) {
        builder.push("general");
        builder.comment("Whether to display disclaimer GUI");
        DISCLAIMER_SHOW = builder.define("DisclaimerShow", true);
        builder.comment("Whether to print animation roulette play message");
        PRINT_ANIMATION_ROULETTE_MSG = builder.define("PrintAnimationRouletteMsg", false);
        builder.comment("Prevents rendering of self player's model");
        DISABLE_SELF_MODEL = builder.define("DisableSelfModel", false);
        builder.comment("Prevents rendering of other player's model");
        DISABLE_OTHER_MODEL = builder.define("DisableOtherModel", false);
        builder.comment("Prevents rendering of self player's hand");
        DISABLE_SELF_HANDS = builder.define("DisableSelfHands", false);
        builder.comment("Prevents rendering of projectile model");
        DISABLE_PROJECTILE_MODEL = builder.define("DisableProjectileModel", false);
        builder.comment("Prevents rendering of vehicle model");
        DISABLE_VEHICLE_MODEL = builder.define("DisableVehicleModel", false);
        builder.comment("Disable first person animation from other mods.");
        DISABLE_EXTERNAL_FP_ANIM = builder.define("DisableExternalFirstPersonAnim", false);
        builder.comment("If rendering errors occur, try turning on this.");
        USE_COMPATIBILITY_RENDERER = builder.define("UseCompatibilityRenderer", false);
        builder.comment("Test renderer.");
        USE_GPU_RENDERER = builder.define("UseGpuRenderer", true);
        builder.comment("Force GPU renderer even when GL extensions are under-reported (MobileGlues/GLES translation layers).");
        FORCE_GPU_RENDERER = builder.define("ForceGpuRenderer", true);
        LAZY_MODEL_LOADING = builder.define("LazyModelLoading", true);
        builder.comment("Always use client-only mode");
        FORCE_CLIENT_MODE = builder.define("ForceClientMode", false);
        builder.comment("Seconds to wait for the server to answer the handshake.");
        HANDSHAKE_TIMEOUT = builder.defineInRange("HandshakeTimeout", 5.0d, 1.0d, 60.0d);
        builder.comment("Maximum entries shown at search list.");
        SEARCH_SUGGESTION_COUNT = builder.defineInRange("SearchSuggestionCount", 8.0d, 1.0d, 30.0d);
        ROULETTE_SETTINGS_MODE = builder.defineEnum("RouletteSettingsMode", RouletteSettingsMode.MODERN);
        ROULETTE_MODE = builder.defineEnum("RouletteMode", RouletteMode.CLASSIC);
        BLUR_GUI = builder.define("BlurGui", true);
        TEXTURE_SCREEN_MODE = builder.defineEnum("TextureScreenMode", TextureScreenMode.MODERN);
        MODEL_INFO_SCREEN_MODE = builder.defineEnum("ModelInfoScreenMode", ModelInfoScreenMode.MODERN);
        builder.comment("The amount of volume when the animation is played.");
        SOUND_VOLUME = builder.defineInRange("SoundVolume", 100.0d, 0.0d, 100.0d);
        builder.comment("Whether to display model ID first in the model selection screen, instead of the model name filled in by the model author.");
        SHOW_MODEL_ID_FIRST = builder.define("ShowModelIdFirst", false);
        builder.pop();
        builder.push("Integration");
        SOPHISTICATEDBACKPACK = builder.define("SophisticatedBackpack", true);
        PARCOOL = builder.define("Parcool", true);
        builder.pop();
    }
}
