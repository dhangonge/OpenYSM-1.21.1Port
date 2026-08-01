package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.upload.ModelUploadSession;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record S2CModelUploadStartPacket(long uploadId, byte status, int chunkSize, int maxTotalBytes, int chunksPerTick, String message) implements CustomPacketPayload, IPayloadHandler<S2CModelUploadStartPacket> {

    public static final Type<S2CModelUploadStartPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2cmodeluploadstart"));

    public static final StreamCodec<FriendlyByteBuf, S2CModelUploadStartPacket> STREAM_CODEC =
            StreamCodec.of(S2CModelUploadStartPacket::encode, S2CModelUploadStartPacket::decode);

    private static void encode(FriendlyByteBuf buf, S2CModelUploadStartPacket packet) {
        buf.writeVarLong(packet.uploadId);
        buf.writeByte(packet.status);
        buf.writeVarInt(packet.chunkSize);
        buf.writeVarInt(packet.maxTotalBytes);
        buf.writeVarInt(packet.chunksPerTick);
        buf.writeUtf(packet.message);
    }

    private static S2CModelUploadStartPacket decode(FriendlyByteBuf buf) {
        long uploadId = buf.readVarLong();
        byte status = buf.readByte();
        int chunkSize = buf.readVarInt();
        int maxTotalBytes = buf.readVarInt();
        int chunksPerTick = buf.readVarInt();
        String message = buf.readUtf();
        return new S2CModelUploadStartPacket(uploadId, status, chunkSize, maxTotalBytes, chunksPerTick, message);
    }

    @Override
    public Type<S2CModelUploadStartPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CModelUploadStartPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleOnClient(payload));
        } else {
            context.enqueueWork(() -> handleOnServer(payload));
        }
    }

    private static void handleOnClient(S2CModelUploadStartPacket packet) {
        ModelUploadSession.onStartAck(packet.uploadId, packet.status, packet.chunkSize, packet.maxTotalBytes, packet.chunksPerTick, packet.message);
    }

    private static void handleOnServer(S2CModelUploadStartPacket packet) {
    }
}
