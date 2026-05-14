package com.elfmcys.yesstevemodel.client.input;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.renderer.AnimationDebugOverlay;
import com.elfmcys.yesstevemodel.util.InputUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class DebugAnimationKey {

    public static final KeyMapping KEY_MAPPING = new KeyMapping("key.yes_steve_model.debug_animation.desc", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, 66, "key.category.yes_steve_model");

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (YesSteveModel.isAvailable() && InputUtil.isPlayerReady() && event.getAction() == 1 && InputUtil.isKeyPressed(event, KEY_MAPPING)) {
            if (!AnimationDebugOverlay.isDebugActive()) {
                AnimationDebugOverlay.tryUpdateFromHitResult();
            } else {
                AnimationDebugOverlay.clearActiveModel();
            }
        }
    }
}