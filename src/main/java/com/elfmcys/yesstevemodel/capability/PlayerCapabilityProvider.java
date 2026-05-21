package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.client.player.LocalPlayer;
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
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class PlayerCapabilityProvider implements ICapabilityProvider<Entity, Void, PlayerCapability> {

    public static final EntityCapability<PlayerCapability, Void> PLAYER_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "animatable"), PlayerCapability.class);

    public static final PlayerCapabilityProvider INSTANCE = new PlayerCapabilityProvider();

    private final ConcurrentHashMap<Integer, PlayerCapability> cache = new ConcurrentHashMap<>();

    private static volatile String persistedModelId;
    private static volatile String persistedTextureName;

    public static void savePersistedModel(String modelId, String textureName) {
        persistedModelId = modelId;
        persistedTextureName = textureName;
    }

    private PlayerCapabilityProvider() {}

    @Override
    @Nullable
    public PlayerCapability getCapability(Entity entity, Void context) {
        if (entity instanceof Player player) {
            //修复了尸体模组共用模型的bug
            PlayerCapability existing = cache.get(entity.getId());
            if (existing != null && existing.getEntity() != entity) {
                PlayerCapability old = cache.putIfAbsent(entity.getId(), null);
                if(old != null) {
                    cache.put(old.getEntity().getId(),old);
                }
                cache.remove(entity.getId());
            }
            return cache.computeIfAbsent(entity.getId(), uuid -> {
                PlayerCapability cap = new PlayerCapability(player);
                if (player instanceof LocalPlayer && persistedModelId != null) {
                    cap.initModelWithTexture(persistedModelId, persistedTextureName);
                }
                return cap;
            });
        }
        return null;
    }

    public void invalidate(int id) {
        cache.remove(id);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
    private static class CleanupHandler {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            INSTANCE.invalidate(event.getEntity().getId());
        }

        @SubscribeEvent
        public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            var player = event.getPlayer();
            if (player != null) {
                PlayerCapability cap = player.getCapability(PLAYER_CAP);
                if (cap != null && cap.isModelReady()) {
                    savePersistedModel(cap.getModelId(), cap.currentTextureName);
                }
            }
        }
    }
}
