package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event;

import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.event.YsmMaidClientTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MaidClientTickEvent {
    @SubscribeEvent
    public void onMaidClientTick(YsmMaidClientTickEvent event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }
        EntityMaid maid = event.getMaid();
        if (localPlayer.getUUID().equals(maid.getOwnerUUID())) {
            tickMaidModel(maid);
        }
    }

    private void tickMaidModel(EntityMaid entityMaid) {
        // 上游为 ifPresent(cap -> {})：cap 存在时才有后续（当前为空实现）；cap 不存在时无事可做
        MaidCapability cap = entityMaid.getCapability(MaidCapabilityProvider.MAID_CAP);
        if (cap == null) {
            return;
        }
    }
}