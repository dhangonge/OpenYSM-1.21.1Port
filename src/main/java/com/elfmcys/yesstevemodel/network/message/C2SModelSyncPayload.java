package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.nio.ByteBuffer;

public class C2SModelSyncPayload implements CustomPacketPayload, IPayloadHandler<C2SModelSyncPayload> {

    public static final CustomPacketPayload.Type<C2SModelSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_model_sync"));

    public static final StreamCodec<FriendlyByteBuf, C2SModelSyncPayload> STREAM_CODEC =
            StreamCodec.of(C2SModelSyncPayload::encode, C2SModelSyncPayload::decode);

    private final byte[] data;

    public C2SModelSyncPayload(byte[] data) {
        this.data = data;
    }

    public static void encode(FriendlyByteBuf buf, C2SModelSyncPayload message) {
        buf.writeByteArray(message.data);
    }

    public static C2SModelSyncPayload decode(FriendlyByteBuf buf) {
        return new C2SModelSyncPayload(buf.readByteArray());
    }

    @Override
    public Type<C2SModelSyncPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SModelSyncPayload payload, IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        if (context.flow().isServerbound() && sender != null) {
            ServerModelManager.nativeSendModelData(sender.getUUID(), ByteBuffer.wrap(payload.data));
        }
    }
}
