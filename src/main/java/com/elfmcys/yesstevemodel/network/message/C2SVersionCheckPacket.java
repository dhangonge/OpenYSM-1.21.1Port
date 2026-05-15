package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapability;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.StarModelsCapability;
import com.elfmcys.yesstevemodel.capability.StarModelsCapabilityProvider;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class C2SVersionCheckPacket implements CustomPacketPayload, IPayloadHandler<C2SVersionCheckPacket> {

    public static final CustomPacketPayload.Type<C2SVersionCheckPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_version_check"));

    public static final StreamCodec<FriendlyByteBuf, C2SVersionCheckPacket> STREAM_CODEC =
            StreamCodec.of(C2SVersionCheckPacket::encode, C2SVersionCheckPacket::decode);

    private final String version;

    public C2SVersionCheckPacket() {
        this(NetworkHandler.VERSION);
    }

    public C2SVersionCheckPacket(String version) {
        this.version = version;
    }

    public static C2SVersionCheckPacket decode(FriendlyByteBuf buf) {
        return new C2SVersionCheckPacket(buf.readUtf());
    }

    public static void encode(FriendlyByteBuf buf, C2SVersionCheckPacket message) {
        buf.writeUtf(message.version);
    }

    @Override
    public Type<C2SVersionCheckPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SVersionCheckPacket payload, IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        if (sender != null && NetworkHandler.setChannelVersion(sender.connection.getConnection(), payload.version)) {
            ServerModelManager.validatePlayerModel(sender);
            ModelInfoCapability cap = sender.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
            if (cap != null) {
                cap.setMandatory(false);
                cap.stopAnimation(sender);
            }
            AuthModelsCapability authCap = sender.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null);
            if (authCap != null) {
                NetworkHandler.sendToClientPlayer(new S2CSyncAuthModelsPacket(authCap.getAuthModels()), sender);
            }
            StarModelsCapability starCap = sender.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null);
            if (starCap != null) {
                NetworkHandler.sendToClientPlayer(new S2CSyncStarModelsPacket(starCap.getStarModels()), sender);
            }
            ServerModelManager.requestPlayerAuth(sender, null);
        }
    }
}
