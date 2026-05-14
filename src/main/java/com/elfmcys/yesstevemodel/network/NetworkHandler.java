package com.elfmcys.yesstevemodel.network;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.network.message.*;
import io.netty.util.AttributeKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public final class NetworkHandler {

    public static final String VERSION = "2.6.0";

    public static final ResourceLocation CHANNEL_ID = ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, VERSION.replace('.', '_'));

    private static final AttributeKey<String> CHANNEL_VERSION_KEY = AttributeKey.valueOf("yes_steve_model_channel_version");

    public static boolean setChannelVersion(Connection connection, String str) {
        return connection.channel().attr(CHANNEL_VERSION_KEY).compareAndSet(null, str);
    }

    public static boolean isPlayerConnected(ServerPlayer serverPlayer) {
        return serverPlayer.connection != null && isConnectionValid(serverPlayer.connection.connection);
    }

    public static boolean isClientConnected() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return false;
        }
        return isConnectionValid(connection.getConnection());
    }

    public static boolean isConnectionValid(@Nullable Connection connection) {
        return connection != null && connection.channel() != null && VERSION.equals(connection.channel().attr(CHANNEL_VERSION_KEY).get());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(YesSteveModel.MOD_ID).versioned(VERSION);
        registrar.playToClient(S2CModelSyncPayload.TYPE, S2CModelSyncPayload.STREAM_CODEC, S2CModelSyncPayload::handle);
        registrar.playToServer(C2SModelSyncPayload.TYPE, C2SModelSyncPayload.STREAM_CODEC, C2SModelSyncPayload::handle);
        registrar.playToClient(S2CExecuteMolangPacket.TYPE, S2CExecuteMolangPacket.STREAM_CODEC, S2CExecuteMolangPacket::handle);
        registrar.playToClient(S2CSetModelAndTexturePacket.TYPE, S2CSetModelAndTexturePacket.STREAM_CODEC, S2CSetModelAndTexturePacket::handle);
        registrar.playToServer(C2SRequestSwitchModelPacket.TYPE, C2SRequestSwitchModelPacket.STREAM_CODEC, C2SRequestSwitchModelPacket::handle);
        registrar.playToClient(S2CSyncAuthModelsPacket.TYPE, S2CSyncAuthModelsPacket.STREAM_CODEC, S2CSyncAuthModelsPacket::handle);
        registrar.playToServer(C2SPlayAnimationPacket.TYPE, C2SPlayAnimationPacket.STREAM_CODEC, C2SPlayAnimationPacket::handle);
        registrar.playToClient(S2CSyncStarModelsPacket.TYPE, S2CSyncStarModelsPacket.STREAM_CODEC, S2CSyncStarModelsPacket::handle);
        registrar.playToServer(C2SSetStarModelPacket.TYPE, C2SSetStarModelPacket.STREAM_CODEC, C2SSetStarModelPacket::handle);
        registrar.playToServer(C2SCompleteFeedbackPacket.TYPE, C2SCompleteFeedbackPacket.STREAM_CODEC, C2SCompleteFeedbackPacket::handle);
        registrar.playToClient(S2CSyncProjectileModelPacket.TYPE, S2CSyncProjectileModelPacket.STREAM_CODEC, S2CSyncProjectileModelPacket::handle);
        registrar.playToServer(C2SRequestExecuteMolangPacket.TYPE, C2SRequestExecuteMolangPacket.STREAM_CODEC, C2SRequestExecuteMolangPacket::handle);
        registrar.playToServer(C2SSyncAnimationExpressionPacket.TYPE, C2SSyncAnimationExpressionPacket.STREAM_CODEC, C2SSyncAnimationExpressionPacket::handle);
        registrar.playToClient(S2CSyncAnimationExpressionPacket.TYPE, S2CSyncAnimationExpressionPacket.STREAM_CODEC, S2CSyncAnimationExpressionPacket::handle);
        registrar.playToClient(S2CSyncPlayerStatePacket.TYPE, S2CSyncPlayerStatePacket.STREAM_CODEC, S2CSyncPlayerStatePacket::handle);
        registrar.playToClient(S2CSyncVehicleModelPacket.TYPE, S2CSyncVehicleModelPacket.STREAM_CODEC, S2CSyncVehicleModelPacket::handle);
        registrar.playToServer(C2SSwingArmPacket.TYPE, C2SSwingArmPacket.STREAM_CODEC, C2SSwingArmPacket::handle);
        registrar.playToClient(S2CVersionCheckPacket.TYPE, S2CVersionCheckPacket.STREAM_CODEC, S2CVersionCheckPacket::handle);
        registrar.playToServer(C2SVersionCheckPacket.TYPE, C2SVersionCheckPacket.STREAM_CODEC, C2SVersionCheckPacket::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        if (isClientConnected()) {
            PacketDistributor.sendToServer(payload);
        }
    }

    public static void sendToClientPlayer(CustomPacketPayload payload, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, payload);
    }

    public static void sendToAll(CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }

    public static void sendToTrackingEntity(CustomPacketPayload payload, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }

    public static void sendToTrackingEntityAndSelf(CustomPacketPayload payload, Player player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf((ServerPlayer) player, payload);
    }
}
