package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class MaidCapabilityProvider implements ICapabilityProvider<Entity, Void, MaidCapability> {

    public static final EntityCapability<MaidCapability, Void> MAID_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "ysm_maid"), MaidCapability.class);

    public static final MaidCapabilityProvider INSTANCE = new MaidCapabilityProvider();

    private final ConcurrentHashMap<UUID, MaidCapability> cache = new ConcurrentHashMap<>();

    private MaidCapabilityProvider() {}

    @Override
    @Nullable
    public MaidCapability getCapability(Entity entity, Void context) {
        if (entity instanceof EntityMaid maid) {
            return cache.computeIfAbsent(entity.getUUID(), uuid -> new MaidCapability(maid, true));
        }
        return null;
    }

    public void invalidate(UUID uuid) {
        cache.remove(uuid);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
    private static class CleanupHandler {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            INSTANCE.invalidate(event.getEntity().getUUID());
        }
    }
}
