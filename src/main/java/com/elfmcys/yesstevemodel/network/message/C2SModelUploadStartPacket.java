package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record C2SModelUploadStartPacket(String modelId, int totalBytes, String sha256)
        implements CustomPacketPayload, IPayloadHandler<C2SModelUploadStartPacket> {

    public static final Type<C2SModelUploadStartPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_model_upload_start"));

    public static final StreamCodec<FriendlyByteBuf, C2SModelUploadStartPacket> STREAM_CODEC =
            StreamCodec.of(C2SModelUploadStartPacket::encode, C2SModelUploadStartPacket::decode);

    private static void encode(FriendlyByteBuf buf, C2SModelUploadStartPacket packet) {
        buf.writeUtf(packet.modelId);
        buf.writeVarInt(packet.totalBytes);
        buf.writeUtf(packet.sha256);
    }

    private static C2SModelUploadStartPacket decode(FriendlyByteBuf buf) {
        return new C2SModelUploadStartPacket(buf.readUtf(), buf.readVarInt(), buf.readUtf());
    }

    @Override
    public Type<C2SModelUploadStartPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SModelUploadStartPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> handleOnServer(payload));
        }
    }

    private static void handleOnServer(C2SModelUploadStartPacket packet) {
    }
}
