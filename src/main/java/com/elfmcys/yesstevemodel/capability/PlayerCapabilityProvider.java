package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
public final class PlayerCapabilityProvider implements ICapabilityProvider<Entity, Void, PlayerCapability> {

    public static final EntityCapability<PlayerCapability, Void> PLAYER_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "animatable"), PlayerCapability.class);

    public static final PlayerCapabilityProvider INSTANCE = new PlayerCapabilityProvider();

    private final ConcurrentHashMap<UUID, PlayerCapability> cache = new ConcurrentHashMap<>();

    private PlayerCapabilityProvider() {}

    @Override
    @Nullable
    public PlayerCapability getCapability(Entity entity, Void context) {
        if (entity instanceof Player player) {
            return cache.computeIfAbsent(entity.getUUID(), uuid -> new PlayerCapability(player));
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
