package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.YesSteveModel;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class ServerStartupEvent {
    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        ServerModelManager.THE_SERVER = event.getServer();
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        ServerModelManager.loadModels(result -> {
            if (!result.isSuccess()) {
                event.getServer().execute(() -> {
                    throw new RuntimeException("YSM Loading Failed: " + result.getErrorMessage().getString(256));
                });
            }
        }, null);
    }
}