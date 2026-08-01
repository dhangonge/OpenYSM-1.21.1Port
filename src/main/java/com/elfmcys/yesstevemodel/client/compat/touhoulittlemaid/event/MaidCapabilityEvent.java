package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
public final class MaidCapabilityEvent {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!YesSteveModel.isAvailable()||!TouhouMaidCompat.isLoaded()) {
            return;
        }
        // 所有 mod 构造完成后才解析真实 capability，避免类加载期顺序竞态绑定 dummy
        MaidCapabilityProvider.ensureResolved();
        event.registerEntity(MaidCapabilityProvider.MAID_CAP, EntityMaid.TYPE, MaidCapabilityProvider.INSTANCE);
    }
}
