package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.client.Minecraft;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class PlayerCapabilityProvider implements ICapabilityProvider<Entity, Void, PlayerCapability> {

    public static final EntityCapability<PlayerCapability, Void> PLAYER_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "animatable"), PlayerCapability.class);

    public static final PlayerCapabilityProvider INSTANCE = new PlayerCapabilityProvider();

    private final ConcurrentHashMap<UUID, PlayerCapability> cache = new ConcurrentHashMap<>();

    private static volatile String persistedModelId;
    private static volatile String persistedTextureName;

    public static void savePersistedModel(String modelId, String textureName) {
        persistedModelId = modelId;
        persistedTextureName = textureName;
    }

    private PlayerCapabilityProvider() {}

    @Override
    @Nullable
    public PlayerCapability getCapability(@NotNull Entity entity, Void context) {
        if (entity instanceof Player player) {
            //修复了尸体模组共用模型的bug
            PlayerCapability existing = cache.get(entity.getUUID());
            if(existing != null &&!existing.isActive())
                return existing;
            if ( existing != null && existing.getEntity() != Minecraft.getInstance().player) {
                YesSteveModel.LOGGER.info("Player capability already exists for entity {}, Type {}, named {},isAlive: {}" , existing.getEntity().getUUID(),existing.getEntity().getType(),existing.getEntity().getName(),existing.getEntity().isAlive());
                YesSteveModel.LOGGER.info("Current Player is {}, Type {}, named {},isAlive: {}" , entity.getUUID(),entity.getType(),entity.getName(),entity.isAlive());
                cache.remove(entity.getUUID());
                UUID newUUID = UUID.randomUUID();
                existing.getEntity().setUUID(newUUID);
                PlayerCapability copyCap = new PlayerCapability(existing.getEntity(),false);
                copyCap.initModelWithTexture(persistedModelId,persistedTextureName);
                cache.put(existing.getEntity().getUUID(), copyCap);

            }
            return cache.computeIfAbsent(entity.getUUID(), uuid -> {
                PlayerCapability cap = new PlayerCapability(player);
                if (player instanceof LocalPlayer && persistedModelId != null) {
                    cap.initModelWithTexture(persistedModelId, persistedTextureName);
                }
                return cap;
            });
        }
        return null;
    }

    public void invalidate(UUID UUID) {
        cache.remove(UUID);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
    private static class CleanupHandler {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            INSTANCE.invalidate(event.getEntity().getUUID());
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
