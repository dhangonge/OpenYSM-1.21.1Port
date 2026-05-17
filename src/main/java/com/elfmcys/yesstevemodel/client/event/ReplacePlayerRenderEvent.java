package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.client.compat.firstperson.FirstPersonCompat;
import com.elfmcys.yesstevemodel.client.compat.playeranimator.PlayerAnimatorCompat;
import com.elfmcys.yesstevemodel.client.compat.realcamera.RealCameraCompat;
import com.elfmcys.yesstevemodel.client.renderer.RendererManager;
import com.elfmcys.yesstevemodel.config.GeneralConfig;
import com.elfmcys.yesstevemodel.util.CameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ReplacePlayerRenderEvent {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        Player entity = event.getEntity();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (entity.equals(localPlayer) && GeneralConfig.DISABLE_SELF_MODEL.get().booleanValue()) {
            return;
        }
        if ((!entity.equals(localPlayer) && GeneralConfig.DISABLE_OTHER_MODEL.get().booleanValue()) || event.getEntity().isSpectator()) {
            return;
        }
        PlayerCapability cap = entity.getCapability(PlayerCapabilityProvider.PLAYER_CAP);
        if (cap != null && cap.isModelActive()) {
            if (!CameraUtil.isFirstPerson(cap) || FirstPersonCompat.isFirstPersonActive() || RealCameraCompat.isActive() || GeneralConfig.DISABLE_EXTERNAL_FP_ANIM.get().booleanValue() || PlayerAnimatorCompat.isPlayerAnimated(localPlayer)) {
                event.setCanceled(true);
                RendererManager.getPlayerRenderer().render(event.getEntity(), event.getEntity().getYRot(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            }
        }
    }
}