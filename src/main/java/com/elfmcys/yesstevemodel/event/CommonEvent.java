package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.*;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.io.IOException;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class CommonEvent {
    public static Object nativeInit() {
        ClientModelManager.loadDefaultModel();
        try {
            ServerModelManager.reloadPacks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!YesSteveModel.isAvailable()) {
            event.enqueueWork(() -> {
                ModLoader.get().addWarning(YesSteveModel.getLoadingWarning());
            });
        } else {
            event.enqueueWork(() -> {
                TouhouMaidCompat.init();
                nativeInit();
            });
        }
    }
}
