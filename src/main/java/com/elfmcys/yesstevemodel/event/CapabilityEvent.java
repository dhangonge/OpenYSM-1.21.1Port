package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.*;
import com.elfmcys.yesstevemodel.config.ServerConfig;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import com.elfmcys.yesstevemodel.network.message.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber
public final class CapabilityEvent {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }

        event.registerEntity(ModelInfoCapabilityProvider.MODEL_INFO_CAP, EntityType.PLAYER, ModelInfoCapabilityProvider.INSTANCE);
        event.registerEntity(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, EntityType.PLAYER, AuthModelsCapabilityProvider.INSTANCE);
        event.registerEntity(StarModelsCapabilityProvider.STAR_MODELS_CAP, EntityType.PLAYER, StarModelsCapabilityProvider.INSTANCE);
        event.registerEntity(VehicleModelCapabilityProvider.VEHICLE_MODEL_CAP, EntityType.PLAYER, VehicleModelCapabilityProvider.INSTANCE);
        event.registerEntity(ProjectileModelCapabilityProvider.PROJECTILE_MODEL, EntityType.ARROW, ProjectileModelCapabilityProvider.INSTANCE);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            event.registerEntity(PlayerCapabilityProvider.PLAYER_CAP, EntityType.PLAYER, PlayerCapabilityProvider.INSTANCE);
            event.registerEntity(VehicleCapabilityProvider.VEHICLE_CAP, EntityType.PLAYER, VehicleCapabilityProvider.INSTANCE);
            event.registerEntity(ProjectileCapabilityProvider.PROJECTILE_CAP, EntityType.ARROW, ProjectileCapabilityProvider.INSTANCE);
            event.registerEntity(ClientLazyCapabilityProvider.CLIENT_LAZY_CAP, EntityType.PLAYER, ClientLazyCapabilityProvider.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        ModelInfoCapability oldModelInfoCap = getModelInfoCap(event.getOriginal());
        AuthModelsCapability oldAuthModelsCap = getAuthModelsCap(event.getOriginal());
        StarModelsCapability oldStarModelsCap = getStarModelsCap(event.getOriginal());
        ModelInfoCapability modelInfoCap = getModelInfoCap(event.getEntity());
        AuthModelsCapability authModelsCap = getAuthModelsCap(event.getEntity());
        StarModelsCapability starModelsCap = getStarModelsCap(event.getEntity());
        if (modelInfoCap != null && oldModelInfoCap != null) {
            modelInfoCap.copyFrom(oldModelInfoCap);
        }
        if (authModelsCap != null && oldAuthModelsCap != null) {
            authModelsCap.copyFrom(oldAuthModelsCap);
        }
        if (starModelsCap != null && oldStarModelsCap != null) {
            starModelsCap.copyFrom(oldStarModelsCap);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking startTracking) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        Entity target = startTracking.getTarget();
        if (target instanceof ServerPlayer trackPlayer) {
            Player entity = startTracking.getEntity();
            ModelInfoCapability cap = getModelInfoCap(trackPlayer);
            if (cap != null) {
                if (!NetworkHandler.isPlayerConnected(trackPlayer) && !cap.isMandatory()) {
                    return;
                }
                Optional<S2CSetModelAndTexturePacket> optional = cap.createSyncMessage(trackPlayer, false);
                Consumer<? super S2CSetModelAndTexturePacket> consumer = message -> {
                    NetworkHandler.sendToClientPlayer(message, entity);
                };
                optional.ifPresentOrElse(consumer, cap::markDirty);
            }
            return;
        }
        target = startTracking.getTarget();
        if (target instanceof Projectile projectile) {
            ProjectileModelCapability cap = projectile.getCapability(ProjectileModelCapabilityProvider.PROJECTILE_MODEL, null);
            if (cap != null && cap.isInitialized()) {
                NetworkHandler.sendToClientPlayer(new S2CSyncProjectileModelPacket(projectile.getId(), cap), startTracking.getEntity());
            }
        } else if (startTracking.getTarget() != null) {
            VehicleModelCapability cap = startTracking.getTarget().getCapability(VehicleModelCapabilityProvider.VEHICLE_MODEL_CAP, null);
            if (cap != null && cap.isInitialized()) {
                NetworkHandler.sendToClientPlayer(new S2CSyncVehicleModelPacket(startTracking.getTarget().getId(), cap), startTracking.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            ModelInfoCapability modelInfoCap = getModelInfoCap(player);
            if (modelInfoCap != null) {
                if (!NetworkHandler.isPlayerConnected(player) && !modelInfoCap.isMandatory()) {
                    modelInfoCap.markDirty();
                } else {
                    modelInfoCap.stopAnimation(player);
                    Optional<S2CSetModelAndTexturePacket> optional = modelInfoCap.createSyncMessage(player, false);
                    Consumer<? super S2CSetModelAndTexturePacket> consumer = message -> {
                        NetworkHandler.sendToClientPlayer(message, player);
                    };
                    optional.ifPresentOrElse(consumer, modelInfoCap::markDirty);
                }
            }
            AuthModelsCapability authModelsCap = getAuthModelsCap(player);
            if (authModelsCap != null) {
                for (String modelId : ServerModelManager.getAuthModels()) {
                    authModelsCap.addModel(modelId);
                }
                NetworkHandler.sendToClientPlayer(new S2CSyncAuthModelsPacket(authModelsCap.getAuthModels()), player);
            }
            StarModelsCapability starModelsCap = getStarModelsCap(player);
            if (starModelsCap != null) {
                NetworkHandler.sendToClientPlayer(new S2CSyncStarModelsPacket(starModelsCap.getStarModels()), player);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();
        Boolean lowBandwidth = ServerConfig.LOW_BANDWIDTH_USAGE!=null?ServerConfig.LOW_BANDWIDTH_USAGE.get():false;
        for (ServerPlayer serverPlayer : players) {
            ModelInfoCapability cap = getModelInfoCap(serverPlayer);
            if (cap == null) {
                continue;
            }
            if (!NetworkHandler.isPlayerConnected(serverPlayer) && !cap.isMandatory()) {
                if (serverPlayer.tickCount == 200 || serverPlayer.tickCount == 600 || serverPlayer.tickCount == 1800) {
                    NetworkHandler.sendToClientPlayer(new S2CVersionCheckPacket(), serverPlayer);
                }
                continue;
            }
            if (cap.isDirty()) {
                cap.getAnimSync().updateAndSync(serverPlayer, false, lowBandwidth);
                cap.createSyncMessage(serverPlayer, true).ifPresent(message -> {
                    cap.clearDirty();
                    NetworkHandler.sendToTrackingEntityAndSelf(message, serverPlayer);
                    if (serverPlayer.getVehicle() != null && serverPlayer.getVehicle().getFirstPassenger() == serverPlayer) {
                        syncVehicleModel(serverPlayer.getVehicle(), serverPlayer);
                    }
                });
            } else {
                cap.getAnimSync().updateAndSync(serverPlayer, true, lowBandwidth);
            }
        }
    }

    public static void syncProjectileModel(Projectile projectile, ServerPlayer serverPlayer) {
        ModelInfoCapability modelInfoCap = serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
        if (modelInfoCap != null) {
            if (!NetworkHandler.isPlayerConnected(serverPlayer) && !modelInfoCap.isMandatory()) {
                return;
            }
            ProjectileModelCapability projectileModelCap = projectile.getCapability(ProjectileModelCapabilityProvider.PROJECTILE_MODEL, null);
            if (projectileModelCap != null) {
                modelInfoCap.withMolangVars(object2FloatOpenHashMap -> {
                    projectileModelCap.setModel(modelInfoCap.getModelId(), object2FloatOpenHashMap);
                    NetworkHandler.sendToTrackingEntity(new S2CSyncProjectileModelPacket(projectile.getId(), projectileModelCap), projectile);
                });
            }
        }
    }

    public static void syncVehicleModel(Entity entity, ServerPlayer serverPlayer) {
        ModelInfoCapability modelInfoCap = serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
        if (modelInfoCap != null) {
            if (!NetworkHandler.isPlayerConnected(serverPlayer) && !modelInfoCap.isMandatory()) {
                return;
            }
            VehicleModelCapability vehicleModelCap = entity.getCapability(VehicleModelCapabilityProvider.VEHICLE_MODEL_CAP, null);
            if (vehicleModelCap != null) {
                modelInfoCap.getMolangVars().ifPresent(object2FloatOpenHashMap -> {
                    vehicleModelCap.setModel(modelInfoCap.getModelId(), object2FloatOpenHashMap);
                    NetworkHandler.sendToTrackingEntity(new S2CSyncVehicleModelPacket(entity.getId(), vehicleModelCap), entity);
                });
            }
        }
    }

    @Nullable
    private static ModelInfoCapability getModelInfoCap(Player player) {
        return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
    }

    @Nullable
    private static AuthModelsCapability getAuthModelsCap(Player player) {
        return player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null);
    }

    @Nullable
    private static StarModelsCapability getStarModelsCap(Player player) {
        return player.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null);
    }
}
