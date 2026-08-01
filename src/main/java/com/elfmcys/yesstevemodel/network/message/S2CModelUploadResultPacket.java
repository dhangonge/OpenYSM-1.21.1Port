package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.upload.ModelUploadSession;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record S2CModelUploadResultPacket(long uploadId, byte status, String modelId, long h1, long h2, String message) implements CustomPacketPayload, IPayloadHandler<S2CModelUploadResultPacket> {

    public static final Type<S2CModelUploadResultPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2cmodeluploadresult"));

    public static final StreamCodec<FriendlyByteBuf, S2CModelUploadResultPacket> STREAM_CODEC =
            StreamCodec.of(S2CModelUploadResultPacket::encode, S2CModelUploadResultPacket::decode);

    private static void encode(FriendlyByteBuf buf, S2CModelUploadResultPacket packet) {
        buf.writeVarLong(packet.uploadId);
        buf.writeByte(packet.status);
        buf.writeUtf(packet.modelId);
        buf.writeVarLong(packet.h1);
        buf.writeVarLong(packet.h2);
        buf.writeUtf(packet.message);
    }

    private static S2CModelUploadResultPacket decode(FriendlyByteBuf buf) {
        return new S2CModelUploadResultPacket(buf.readVarLong(), buf.readByte(), buf.readUtf(), buf.readVarLong(), buf.readVarLong(), buf.readUtf());
    }

    @Override
    public Type<S2CModelUploadResultPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CModelUploadResultPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleOnClient(payload));
        } else {
            context.enqueueWork(() -> handleOnServer(payload));
        }
    }

    private static void handleOnClient(S2CModelUploadResultPacket packet) {
        ModelUploadSession.onResult(packet.uploadId, packet.status, packet.modelId, packet.h1, packet.h2, packet.message);
    }

    private static void handleOnServer(S2CModelUploadResultPacket packet) {
    }
}
