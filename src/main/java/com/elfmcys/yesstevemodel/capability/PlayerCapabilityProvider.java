package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class PlayerCapabilityProvider implements ICapabilityProvider<Entity, Void, PlayerCapability> {

    public static final EntityCapability<PlayerCapability, Void> PLAYER_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "animatable"), PlayerCapability.class);

    public static final PlayerCapabilityProvider INSTANCE = new PlayerCapabilityProvider();

    private final Map<Entity, PlayerCapability> cache = Collections.synchronizedMap(new IdentityHashMap<>());

    private static volatile String persistedModelId;
    private static volatile String persistedTextureName;

    public static void savePersistedModel(String modelId, String textureName) {
        persistedModelId = modelId;
        persistedTextureName = textureName;
    }

    private PlayerCapabilityProvider() {
    }

    @Override
    @Nullable
    public PlayerCapability getCapability(@NotNull Entity entity, Void context) {
        if (entity instanceof Player player) {
            PlayerCapability cap = cache.get(player);
            if (cap != null) {
                return cap;
            }
            cap = new PlayerCapability(player);
            if (player instanceof LocalPlayer && persistedModelId != null) {
                cap.initModelWithTexture(persistedModelId, persistedTextureName);
            }
            cache.put(player, cap);
            return cap;
        } else {
            return cache.getOrDefault(entity, null);
        }
    }

    public void invalidate(Entity entity) {
        cache.remove(entity);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
    private static class CleanupHandler {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            INSTANCE.invalidate(event.getEntity());
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
