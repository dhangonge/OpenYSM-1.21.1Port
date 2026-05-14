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
public final class ClientLazyCapabilityProvider implements ICapabilityProvider<Entity, Void, ClientLazyCapability> {

    public static final EntityCapability<ClientLazyCapability, Void> CLIENT_LAZY_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "client_lazy"), ClientLazyCapability.class);

    public static final ClientLazyCapabilityProvider INSTANCE = new ClientLazyCapabilityProvider();

    private final ConcurrentHashMap<UUID, ClientLazyCapability> cache = new ConcurrentHashMap<>();

    private ClientLazyCapabilityProvider() {}

    @Override
    @Nullable
    public ClientLazyCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new ClientLazyCapability(entity));
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
