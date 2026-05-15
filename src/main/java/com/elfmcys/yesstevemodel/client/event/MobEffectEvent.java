package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class MobEffectEvent {
    @SubscribeEvent
    public static void onEffectAdded(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added event) {
        if (!YesSteveModel.isAvailable() || event.getEntity().level().isClientSide()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer) {
            if (event.getEffectInstance().getEffect() != null) {
                MobEffectInstance effectInstance = event.getEffectInstance();
                ModelInfoCapability cap = serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP);
                if (cap != null) {
                    cap.getAnimSync().syncEffectAdded(serverPlayer, effectInstance.getEffect().value(), effectInstance.getAmplifier() + 1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove event) {
        if (!YesSteveModel.isAvailable() || event.getEntity().level().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer) {
            if (event.getEffect() != null) {
                ModelInfoCapability cap = serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP);
                if (cap != null) {
                    cap.getAnimSync().syncEffectRemoved(serverPlayer, event.getEffect().value());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired event) {
        if (!YesSteveModel.isAvailable() || event.getEntity().level().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer) {
            if (event.getEffectInstance() != null && event.getEffectInstance().getEffect() != null) {
                ModelInfoCapability cap = serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP);
                if (cap != null) {
                    cap.getAnimSync().syncEffectRemoved(serverPlayer, event.getEffectInstance().getEffect().value());
                }
            }
        }
    }
}