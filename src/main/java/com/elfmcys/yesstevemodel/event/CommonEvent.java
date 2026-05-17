package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

import java.io.IOException;

@EventBusSubscriber
public final class CommonEvent {

    public static Object nativeInit() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return ClientCommonInit.nativeInit();
        }
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
                YesSteveModel.LOGGER.warn(YesSteveModel.getErrorMessage());
            });
        } else {
            event.enqueueWork(() -> {
                if (FMLEnvironment.dist == Dist.CLIENT) {
                    ClientCommonInit.init();
                }
                try {
                    ServerModelManager.reloadPacks();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
