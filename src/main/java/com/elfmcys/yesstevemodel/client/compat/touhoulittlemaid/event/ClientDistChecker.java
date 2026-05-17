package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.event.YsmMaidClientTickEvent;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@OnlyIn(Dist.CLIENT)
public class ClientDistChecker {
    @SubscribeEvent
    public void voidMethod(YsmMaidClientTickEvent event) {}
}