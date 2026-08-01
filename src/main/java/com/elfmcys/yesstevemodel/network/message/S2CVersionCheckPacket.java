package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.client.ClientOnlyMode;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CVersionCheckPacket implements CustomPacketPayload, IPayloadHandler<S2CVersionCheckPacket> {

    private static final String MODEL_SYNC_FRAGMENT_BRAND = "open_ysm:model_sync_fragments_v1";

    public static final CustomPacketPayload.Type<S2CVersionCheckPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_version_check"));

    public static final StreamCodec<FriendlyByteBuf, S2CVersionCheckPacket> STREAM_CODEC =
            StreamCodec.of(S2CVersionCheckPacket::encode, S2CVersionCheckPacket::decode);

    private final String version;
    private final boolean supportsModelSyncFragments;

    public S2CVersionCheckPacket() {
        this(NetworkHandler.VERSION, true);
    }

    private S2CVersionCheckPacket(String version, boolean supportsModelSyncFragments) {
        this.version = version;
        this.supportsModelSyncFragments = supportsModelSyncFragments;
    }

    public static S2CVersionCheckPacket decode(FriendlyByteBuf buf) {
        String version = buf.readUtf();
        boolean supportsModelSyncFragments = false;
        if (buf.readableBytes() > 0) {
            String brand = buf.readUtf();
            if (brand.equals(MODEL_SYNC_FRAGMENT_BRAND)) {
                supportsModelSyncFragments = true;
            }
        }
        return new S2CVersionCheckPacket(version, supportsModelSyncFragments);
    }

    public static void encode(FriendlyByteBuf buf, S2CVersionCheckPacket message) {
        buf.writeUtf(message.version);
        if (message.supportsModelSyncFragments) {
            buf.writeUtf(MODEL_SYNC_FRAGMENT_BRAND);
        }
    }

    @Override
    public Type<S2CVersionCheckPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CVersionCheckPacket payload, IPayloadContext context) {
        NetworkHandler.setServerSupportsModelSyncFragments(payload.supportsModelSyncFragments);
        if (NetworkHandler.setChannelVersion(context.connection(), payload.version)) {
            context.enqueueWork(() -> {
                ClientOnlyMode.leaveStandalone();
                ClientModelManager.onSyncConnected();
            });
        }
        context.reply(new C2SVersionCheckPacket());
    }
}
