package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CVersionCheckPacket implements CustomPacketPayload, IPayloadHandler<S2CVersionCheckPacket> {

    public static final CustomPacketPayload.Type<S2CVersionCheckPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_version_check"));

    public static final StreamCodec<FriendlyByteBuf, S2CVersionCheckPacket> STREAM_CODEC =
            StreamCodec.of(S2CVersionCheckPacket::encode, S2CVersionCheckPacket::decode);

    private final String version;

    public S2CVersionCheckPacket() {
        this(NetworkHandler.VERSION);
    }

    private S2CVersionCheckPacket(String version) {
        this.version = version;
    }

    public static S2CVersionCheckPacket decode(FriendlyByteBuf buf) {
        return new S2CVersionCheckPacket(buf.readUtf());
    }

    public static void encode(FriendlyByteBuf buf, S2CVersionCheckPacket message) {
        buf.writeUtf(message.version);
    }

    @Override
    public Type<S2CVersionCheckPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CVersionCheckPacket payload, IPayloadContext context) {
        if (NetworkHandler.setChannelVersion(context.player().connection.getConnection(), payload.version)) {
            context.enqueueWork(() -> ClientModelManager.onSyncConnected());
        }
        context.reply(new C2SVersionCheckPacket());
    }
}
