package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.client.upload.UploadManager;
import com.elfmcys.yesstevemodel.client.upload.ModelUploadSession;
import com.elfmcys.yesstevemodel.audio.ObjectPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class ClientTickEvent {

    private static int tickCount;

    private static int refreshRate = 60;

    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        tickCount++;
        UploadManager.processPendingUploads();
        ModelUploadSession.tickCurrent();
        ClientModelManager.flushPendingModels();
        ObjectPool.cleanup();
        refreshRate = Minecraft.getInstance().getWindow().getRefreshRate();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            var cap = localPlayer.getCapability(PlayerCapabilityProvider.PLAYER_CAP);
            if (cap != null) {
                cap.tickAnimations();
            }
        }
    }

    public static int getTickCount() {
        return tickCount;
    }

    public static int getRefreshRate() {
        return refreshRate;
    }
}