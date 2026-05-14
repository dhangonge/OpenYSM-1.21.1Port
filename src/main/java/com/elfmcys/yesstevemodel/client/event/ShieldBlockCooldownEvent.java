package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = YesSteveModel.MOD_ID)
public class ShieldBlockCooldownEvent {

    public static final String TAG_KEY = "ysm$shield_block_cooldown";

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        event.getEntity().getPersistentData().putInt(TAG_KEY, 5);
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.getPersistentData().contains(TAG_KEY)) {
            int i = entity.getPersistentData().getInt(TAG_KEY);
            if (i > 0) {
                entity.getPersistentData().putInt(TAG_KEY, i - 1);
            } else {
                entity.getPersistentData().remove(TAG_KEY);
            }
        }
    }

    public static boolean isOnCooldown(LivingEntity livingEntity) {
        return livingEntity.getPersistentData().contains(TAG_KEY);
    }
}