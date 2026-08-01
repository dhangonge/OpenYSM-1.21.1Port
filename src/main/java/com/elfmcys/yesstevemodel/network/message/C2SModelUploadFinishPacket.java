package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record C2SModelUploadFinishPacket(long uploadId)
        implements CustomPacketPayload, IPayloadHandler<C2SModelUploadFinishPacket> {

    public static final Type<C2SModelUploadFinishPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_model_upload_finish"));

    public static final StreamCodec<FriendlyByteBuf, C2SModelUploadFinishPacket> STREAM_CODEC =
            StreamCodec.of(C2SModelUploadFinishPacket::encode, C2SModelUploadFinishPacket::decode);

    private static void encode(FriendlyByteBuf buf, C2SModelUploadFinishPacket packet) {
        buf.writeVarLong(packet.uploadId);
    }

    private static C2SModelUploadFinishPacket decode(FriendlyByteBuf buf) {
        return new C2SModelUploadFinishPacket(buf.readVarLong());
    }

    @Override
    public Type<C2SModelUploadFinishPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SModelUploadFinishPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> handleOnServer(payload));
        }
    }

    private static void handleOnServer(C2SModelUploadFinishPacket packet) {
    }
}
