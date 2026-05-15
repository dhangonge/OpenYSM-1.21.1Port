package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.StarModelsCapability;
import com.elfmcys.yesstevemodel.capability.StarModelsCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class C2SSetStarModelPacket implements CustomPacketPayload, IPayloadHandler<C2SSetStarModelPacket> {

    public static final CustomPacketPayload.Type<C2SSetStarModelPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_set_star_model"));

    public static final StreamCodec<FriendlyByteBuf, C2SSetStarModelPacket> STREAM_CODEC =
            StreamCodec.of(C2SSetStarModelPacket::encode, C2SSetStarModelPacket::decode);

    private final String modelId;

    private final boolean isAdd;

    private C2SSetStarModelPacket(String modelId, boolean isAdd) {
        this.modelId = modelId;
        this.isAdd = isAdd;
    }

    public static C2SSetStarModelPacket add(String modelId) {
        return new C2SSetStarModelPacket(modelId, true);
    }

    public static C2SSetStarModelPacket remove(String modelId) {
        return new C2SSetStarModelPacket(modelId, false);
    }

    public static void encode(FriendlyByteBuf buf, C2SSetStarModelPacket message) {
        buf.writeUtf(message.modelId);
        buf.writeBoolean(message.isAdd);
    }

    public static C2SSetStarModelPacket decode(FriendlyByteBuf buf) {
        return new C2SSetStarModelPacket(buf.readUtf(), buf.readBoolean());
    }

    @Override
    public Type<C2SSetStarModelPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SSetStarModelPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = (ServerPlayer) context.player();
                if (sender == null) {
                    return;
                }
                handleCapability(payload, sender);
            });
        }
    }

    private static void handleCapability(C2SSetStarModelPacket message, ServerPlayer sender) {
        StarModelsCapability cap = sender.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null);
        if (cap != null) {
            if (message.isAdd) {
                cap.addModel(message.modelId);
            } else {
                cap.removeModel(message.modelId);
            }
        }
    }
}
