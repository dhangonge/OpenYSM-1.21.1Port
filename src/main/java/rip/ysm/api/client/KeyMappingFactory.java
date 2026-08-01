package rip.ysm.api.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

/**
 * NeoForge 平台实现（替代上游 Architectury @ExpectPlatform 桩）。
 */
public final class KeyMappingFactory {

    private KeyMappingFactory() {
    }

    public static KeyMapping createInGameAlt(String name, InputConstants.Type type, int keyCode, String category) {
        return new KeyMapping(name, KeyConflictContext.IN_GAME, KeyModifier.ALT, type, keyCode, category);
    }

    public static KeyMapping createInGameNone(String name, InputConstants.Type type, int keyCode, String category) {
        return new KeyMapping(name, KeyConflictContext.IN_GAME, KeyModifier.NONE, type, keyCode, category);
    }

    @SuppressWarnings({"removal"})
    public static boolean isActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
        return keyMapping.matches(keyCode, scanCode) && keyMapping.getKeyModifier().equals(KeyModifier.getActiveModifier());
    }
}
