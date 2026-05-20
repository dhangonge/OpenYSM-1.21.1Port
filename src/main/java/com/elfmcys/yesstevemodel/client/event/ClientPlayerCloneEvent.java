package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber({Dist.CLIENT})
public class ClientPlayerCloneEvent {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        PlayerCapability cap = event.getOriginal().getCapability(PlayerCapabilityProvider.PLAYER_CAP);
        PlayerCapability cap2 = event.getEntity().getCapability(PlayerCapabilityProvider.PLAYER_CAP);
        RenderTickEvent.markClone(cap, cap2);
    }
}