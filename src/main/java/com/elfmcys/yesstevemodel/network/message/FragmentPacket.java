package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

/**
 * 大模型同步分片包（与上游 2.6.6.6 协议一致）。
 * S2C：服务器将超过 30KB 的模型数据切分为多个 FragmentPacket 发送。
 */
public record FragmentPacket(int transferId, int fragmentIndex, int fragmentCount, byte[] data)
        implements CustomPacketPayload, IPayloadHandler<FragmentPacket> {

    public static final int MAX_FRAGMENT_DATA_SIZE = 30_000;

    public static final Type<FragmentPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "model_sync_fragment"));

    public static final StreamCodec<FriendlyByteBuf, FragmentPacket> STREAM_CODEC =
            StreamCodec.of(FragmentPacket::encode, FragmentPacket::decode);

    private static void encode(FriendlyByteBuf buf, FragmentPacket packet) {
        buf.writeVarInt(packet.transferId);
        buf.writeVarInt(packet.fragmentIndex);
        buf.writeVarInt(packet.fragmentCount);
        buf.writeByteArray(packet.data);
    }

    private static FragmentPacket decode(FriendlyByteBuf buf) {
        return new FragmentPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readByteArray(MAX_FRAGMENT_DATA_SIZE));
    }

    @Override
    public Type<FragmentPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(FragmentPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> com.elfmcys.yesstevemodel.network.NetworkHandler.handleClientFragment(payload, context));
        }
    }
}
