package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.VehicleModelCapabilityProvider;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class C2SCompleteFeedbackPacket implements CustomPacketPayload, IPayloadHandler<C2SCompleteFeedbackPacket> {

    public static final CustomPacketPayload.Type<C2SCompleteFeedbackPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_complete_feedback"));

    public static final StreamCodec<FriendlyByteBuf, C2SCompleteFeedbackPacket> STREAM_CODEC =
            StreamCodec.of(C2SCompleteFeedbackPacket::encode, C2SCompleteFeedbackPacket::decode);

    private final FeedbackData feedbackData;

    public C2SCompleteFeedbackPacket(FeedbackData feedbackData) {
        this.feedbackData = feedbackData;
    }

    public FeedbackData feedbackData() {
        return feedbackData;
    }

    public static void encode(FriendlyByteBuf buf, C2SCompleteFeedbackPacket message) {
        FeedbackData.writeToBuf(message.feedbackData, buf);
    }

    public static C2SCompleteFeedbackPacket decode(FriendlyByteBuf buf) {
        return new C2SCompleteFeedbackPacket(FeedbackData.readFromBuf(buf, false));
    }

    @Override
    public Type<C2SCompleteFeedbackPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SCompleteFeedbackPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound() && context.player() != null) {
            ServerPlayer sender = (ServerPlayer) context.player();
            context.enqueueWork(() -> handleOnServer(payload, sender.serverLevel()));
        }
    }

    public static void handleOnServer(C2SCompleteFeedbackPacket message, ServerLevel serverLevel) {
        Entity entity = serverLevel.getEntity(message.feedbackData.flags());
        if (TouhouMaidCompat.isMaidEntity(entity)) {
            TouhouMaidCompat.applyFeedback(entity, message.feedbackData);
        } else if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null).ifPresent(cap -> {
                cap.applyFeedback(serverPlayer, message.feedbackData);
                if (serverPlayer.getVehicle() != null && serverPlayer.getVehicle().getFirstPassenger() == serverPlayer) {
                    serverPlayer.getVehicle().getCapability(VehicleModelCapabilityProvider.VEHICLE_MODEL_CAP, null).ifPresent(vehicleCap -> {
                        cap.getMolangVars().ifPresent(map -> vehicleCap.setModel(cap.getModelId(), map));
                    });
                }
            });
        }
    }
}
