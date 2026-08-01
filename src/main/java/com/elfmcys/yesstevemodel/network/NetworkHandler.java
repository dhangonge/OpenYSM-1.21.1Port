package com.elfmcys.yesstevemodel.network;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.network.message.*;
import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = YesSteveModel.MOD_ID)
public final class NetworkHandler {

    public static final String VERSION = "2.6.0";

    public static final ResourceLocation CHANNEL_ID = ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, VERSION.replace('.', '_'));

    private static final AttributeKey<String> CHANNEL_VERSION_KEY = AttributeKey.valueOf("yes_steve_model_channel_version");

    public static boolean setChannelVersion(Connection connection, String str) {
        return connection.channel().attr(CHANNEL_VERSION_KEY).compareAndSet(null, str);
    }

    public static boolean isPlayerConnected(ServerPlayer serverPlayer) {
        return serverPlayer.connection != null && isConnectionValid(serverPlayer.connection.getConnection());
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isClientConnected() {
        var connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection == null) {
            return false;
        }
        return isConnectionValid(connection.getConnection());
    }

    private static volatile boolean serverSupportsModelSyncFragments = false;

    public static void setServerSupportsModelSyncFragments(boolean supported) {
        serverSupportsModelSyncFragments = supported;
    }

    public static boolean serverSupportsModelSyncFragments() {
        return serverSupportsModelSyncFragments;
    }

    public static boolean isConnectionValid(@Nullable Connection connection) {
        return connection != null && connection.channel() != null && VERSION.equals(connection.channel().attr(CHANNEL_VERSION_KEY).get());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(YesSteveModel.MOD_ID).versioned(VERSION);
        registrar.playToClient(S2CModelSyncPayload.TYPE, S2CModelSyncPayload.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SModelSyncPayload.TYPE, C2SModelSyncPayload.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CExecuteMolangPacket.TYPE, S2CExecuteMolangPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSetModelAndTexturePacket.TYPE, S2CSetModelAndTexturePacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SRequestSwitchModelPacket.TYPE, C2SRequestSwitchModelPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncAuthModelsPacket.TYPE, S2CSyncAuthModelsPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SPlayAnimationPacket.TYPE, C2SPlayAnimationPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncStarModelsPacket.TYPE, S2CSyncStarModelsPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SSetStarModelPacket.TYPE, C2SSetStarModelPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SCompleteFeedbackPacket.TYPE, C2SCompleteFeedbackPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncProjectileModelPacket.TYPE, S2CSyncProjectileModelPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SRequestExecuteMolangPacket.TYPE, C2SRequestExecuteMolangPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SSyncAnimationExpressionPacket.TYPE, C2SSyncAnimationExpressionPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncAnimationExpressionPacket.TYPE, S2CSyncAnimationExpressionPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncPlayerStatePacket.TYPE, S2CSyncPlayerStatePacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CSyncVehicleModelPacket.TYPE, S2CSyncVehicleModelPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SSwingArmPacket.TYPE, C2SSwingArmPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CVersionCheckPacket.TYPE, S2CVersionCheckPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SVersionCheckPacket.TYPE, C2SVersionCheckPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(FragmentPacket.TYPE, FragmentPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SModelUploadStartPacket.TYPE, C2SModelUploadStartPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CModelUploadStartPacket.TYPE, S2CModelUploadStartPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SModelUploadChunkPacket.TYPE, C2SModelUploadChunkPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToServer(C2SModelUploadFinishPacket.TYPE, C2SModelUploadFinishPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
        registrar.playToClient(S2CModelUploadResultPacket.TYPE, S2CModelUploadResultPacket.STREAM_CODEC, (payload, ctx) -> payload.handle(payload, ctx));
    }

    @OnlyIn(Dist.CLIENT)
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

    // ===== 大模型同步分片（与上游 2.6.6.6 协议一致）=====

    private static final int MAX_FRAGMENT_COUNT = 128;
    private static final int MAX_REASSEMBLED_SIZE = 2 * 1024 * 1024;
    private static final long FRAGMENT_TIMEOUT_NANOS = 30_000_000_000L;

    private static final java.util.Map<Connection, java.util.Map<Integer, FragmentAccumulator>> incomingFragments = new java.util.concurrent.ConcurrentHashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void handleClientFragment(FragmentPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        long now = System.nanoTime();
        Connection connection = context.connection();
        java.util.Map<Integer, FragmentAccumulator> transfers = incomingFragments.computeIfAbsent(connection, c -> new java.util.concurrent.ConcurrentHashMap<>());
        transfers.entrySet().removeIf(entry -> now - entry.getValue().lastUpdateNanos > FRAGMENT_TIMEOUT_NANOS);

        FragmentAccumulator accumulator = transfers.computeIfAbsent(packet.transferId(), ignored -> new FragmentAccumulator(packet.fragmentCount()));
        byte[] complete = accumulator.add(packet, now);
        if (complete == null) {
            return;
        }
        transfers.remove(packet.transferId());
        if (transfers.isEmpty()) {
            incomingFragments.remove(connection);
        }

        com.elfmcys.yesstevemodel.client.ClientModelManager.startSync(connection, java.nio.ByteBuffer.wrap(complete));
    }

    /** 服务端：将模型包切分为多个 FragmentPacket（支持大模型传输）。 */
    public static java.util.List<CustomPacketPayload> toClientboundPackets(S2CModelSyncPayload payload, java.util.UUID receiver) {
        byte[] data = payload.getData();
        if (data.length <= FragmentPacket.MAX_FRAGMENT_DATA_SIZE) {
            return java.util.List.of(payload);
        }
        java.util.List<CustomPacketPayload> packets = new java.util.ArrayList<>();
        int transferId = (int) (System.nanoTime() & 0x7fffffff);
        int fragmentCount = (data.length + FragmentPacket.MAX_FRAGMENT_DATA_SIZE - 1) / FragmentPacket.MAX_FRAGMENT_DATA_SIZE;
        if (fragmentCount > MAX_FRAGMENT_COUNT) {
            YesSteveModel.LOGGER.warn("[YSM] Model data too large to fragment ({} bytes, {} fragments), sending unfragmented", data.length, fragmentCount);
            return java.util.List.of(payload);
        }
        for (int index = 0; index < fragmentCount; index++) {
            int from = index * FragmentPacket.MAX_FRAGMENT_DATA_SIZE;
            int to = Math.min(from + FragmentPacket.MAX_FRAGMENT_DATA_SIZE, data.length);
            packets.add(new FragmentPacket(transferId, index, fragmentCount, java.util.Arrays.copyOfRange(data, from, to)));
        }
        return packets;
    }

    private static final class FragmentAccumulator {
        private final byte[][] fragments;
        private int received;
        private int totalSize;
        private volatile long lastUpdateNanos = System.nanoTime();

        private FragmentAccumulator(int fragmentCount) {
            if (fragmentCount <= 0 || fragmentCount > MAX_FRAGMENT_COUNT) {
                throw new IllegalArgumentException("Invalid YSM fragment count: " + fragmentCount);
            }
            this.fragments = new byte[fragmentCount][];
        }

        private synchronized byte[] add(FragmentPacket packet, long now) {
            if (packet.fragmentCount() != fragments.length || packet.fragmentIndex() < 0 || packet.fragmentIndex() >= fragments.length) {
                throw new IllegalArgumentException("Inconsistent YSM fragment metadata");
            }
            lastUpdateNanos = now;
            if (fragments[packet.fragmentIndex()] == null) {
                fragments[packet.fragmentIndex()] = packet.data();
                received++;
                totalSize += packet.data().length;
                if (totalSize > MAX_REASSEMBLED_SIZE) {
                    throw new IllegalArgumentException("Fragmented YSM packet exceeds maximum size");
                }
            }
            if (received != fragments.length) {
                return null;
            }
            byte[] merged = new byte[totalSize];
            int offset = 0;
            for (byte[] fragment : fragments) {
                System.arraycopy(fragment, 0, merged, offset, fragment.length);
                offset += fragment.length;
            }
            return merged;
        }
    }
}
