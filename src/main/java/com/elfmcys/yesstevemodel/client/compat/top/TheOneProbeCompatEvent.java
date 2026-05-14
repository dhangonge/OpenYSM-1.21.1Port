package com.elfmcys.yesstevemodel.client.compat.top;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class TheOneProbeCompatEvent {
    @SubscribeEvent
    public static void onInterModEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> {
            return new TheOneProbeEntityProvider();
        });
    }
}