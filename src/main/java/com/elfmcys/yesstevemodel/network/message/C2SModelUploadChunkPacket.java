package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record C2SModelUploadChunkPacket(long uploadId, int offset, byte[] data)
        implements CustomPacketPayload, IPayloadHandler<C2SModelUploadChunkPacket> {

    public static final Type<C2SModelUploadChunkPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_model_upload_chunk"));

    public static final StreamCodec<FriendlyByteBuf, C2SModelUploadChunkPacket> STREAM_CODEC =
            StreamCodec.of(C2SModelUploadChunkPacket::encode, C2SModelUploadChunkPacket::decode);

    private static void encode(FriendlyByteBuf buf, C2SModelUploadChunkPacket packet) {
        buf.writeVarLong(packet.uploadId);
        buf.writeVarInt(packet.offset);
        buf.writeByteArray(packet.data);
    }

    private static C2SModelUploadChunkPacket decode(FriendlyByteBuf buf) {
        return new C2SModelUploadChunkPacket(buf.readVarLong(), buf.readVarInt(), buf.readByteArray());
    }

    @Override
    public Type<C2SModelUploadChunkPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SModelUploadChunkPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> handleOnServer(payload));
        }
    }

    private static void handleOnServer(C2SModelUploadChunkPacket packet) {
    }
}
