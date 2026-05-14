package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.config.ServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class C2SRequestSwitchModelPacket implements CustomPacketPayload, IPayloadHandler<C2SRequestSwitchModelPacket> {

    public static final CustomPacketPayload.Type<C2SRequestSwitchModelPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_request_switch_model"));

    public static final StreamCodec<FriendlyByteBuf, C2SRequestSwitchModelPacket> STREAM_CODEC =
            StreamCodec.of(C2SRequestSwitchModelPacket::encode, C2SRequestSwitchModelPacket::decode);

    private final String modelId;

    private final String textureId;

    public C2SRequestSwitchModelPacket(String modelId, String textureId) {
        this.modelId = modelId;
        this.textureId = textureId;
    }

    public static void encode(FriendlyByteBuf buf, C2SRequestSwitchModelPacket message) {
        buf.writeUtf(message.modelId);
        buf.writeUtf(message.textureId);
    }

    public static C2SRequestSwitchModelPacket decode(FriendlyByteBuf buf) {
        return new C2SRequestSwitchModelPacket(buf.readUtf(), buf.readUtf());
    }

    @Override
    public Type<C2SRequestSwitchModelPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SRequestSwitchModelPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender != null && ServerConfig.CAN_SWITCH_MODEL.get()) {
                    handleCapability(payload, sender);
                }
            });
        }
    }

    private static void handleCapability(C2SRequestSwitchModelPacket message, ServerPlayer sender) {
        sender.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null).ifPresent(cap -> {
            sender.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null).ifPresent(cap2 -> {
                String str = message.modelId;
                if (!ServerModelManager.getServerModelInfo().containsKey(str) || ((ServerModelManager.getAuthModels().contains(str) && !cap2.containsModel(message.modelId)) || !ServerModelManager.getServerModelInfo().get(str).getModelInfo().getTextures().contains(message.textureId))) {
                    cap.resetToDefault();
                } else {
                    cap.setModelAndTexture(message.modelId, message.textureId);
                }
                cap.stopAnimation(sender);
            });
        });
    }
}
