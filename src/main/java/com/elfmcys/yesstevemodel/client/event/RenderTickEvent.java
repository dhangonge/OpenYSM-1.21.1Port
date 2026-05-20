package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 1.21.1把大量有关克隆玩家的API修改成了仅迁移玩家，这个时候使用旧的ClientPlayerCloneEvent会发生严重的模型数据丢失问题
 * 但是新的事件不是由RenderThread调用的，因此会导致跨线程调用渲染，为了解决这个问题采用脏标记+渲染事件
 */
@EventBusSubscriber({Dist.CLIENT})
public class RenderTickEvent extends Event {

    private static final AtomicBoolean cloneDirtyMark = new AtomicBoolean(false);

    private static PlayerCapability capOrigin;
    private static PlayerCapability capTarget;

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        RenderSystem.recordRenderCall(
                () -> {
                    //consume
                    if(cloneDirtyMark.getAndSet(false )) {
                        if (capOrigin != null && capTarget != null) {
                            capTarget.copyFrom(capOrigin);
                        }
                        capOrigin = null;
                        capTarget = null;
                    }
                }
        );
    }

    public static void markClone(PlayerCapability capOrigin, PlayerCapability capTarget) {
        RenderTickEvent.capOrigin = capOrigin;
        RenderTickEvent.capTarget = capTarget;
        cloneDirtyMark.set(true);
    }

}
