package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.nio.ByteBuffer;

public class S2CModelSyncPayload implements CustomPacketPayload, IPayloadHandler<S2CModelSyncPayload> {

    public static final CustomPacketPayload.Type<S2CModelSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_model_sync"));

    public static final StreamCodec<FriendlyByteBuf, S2CModelSyncPayload> STREAM_CODEC =
            StreamCodec.of(S2CModelSyncPayload::encode, S2CModelSyncPayload::decode);

    private final ByteBuffer data;

    public S2CModelSyncPayload(ByteBuffer data) {
        this.data = data;
    }

    public static void encode(FriendlyByteBuf buf, S2CModelSyncPayload message) {
        buf.writeBytes(message.data);
    }

    public static S2CModelSyncPayload decode(FriendlyByteBuf buf) {
        ByteBuffer data = ByteBuffer.allocateDirect(buf.readableBytes());
        buf.readBytes(data);
        return new S2CModelSyncPayload(data);
    }

    @Override
    public Type<S2CModelSyncPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CModelSyncPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            ClientModelManager.startSync(context.connection(), payload.data);
        }
    }
}
