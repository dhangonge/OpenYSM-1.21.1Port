package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
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
public final class VehicleCapabilityProvider implements ICapabilityProvider<Entity, Void, VehicleCapability> {

    public static final EntityCapability<VehicleCapability, Void> VEHICLE_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "vehicle_animatable"), VehicleCapability.class);

    public static final VehicleCapabilityProvider INSTANCE = new VehicleCapabilityProvider();

    private final ConcurrentHashMap<UUID, VehicleCapability> cache = new ConcurrentHashMap<>();

    private VehicleCapabilityProvider() {}

    @Override
    @Nullable
    public VehicleCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new VehicleCapability(entity));
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
