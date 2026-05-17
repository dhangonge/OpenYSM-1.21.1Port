package com.elfmcys.yesstevemodel.client.compat.top;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber({Dist.CLIENT})
public final class TheOneProbeCompatEvent {
    @SubscribeEvent
    public static void onInterModEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> {
            return new TheOneProbeEntityProvider();
        });
    }
}