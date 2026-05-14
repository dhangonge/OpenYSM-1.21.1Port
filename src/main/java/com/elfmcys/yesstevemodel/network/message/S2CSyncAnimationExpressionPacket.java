package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CSyncAnimationExpressionPacket implements CustomPacketPayload, IPayloadHandler<S2CSyncAnimationExpressionPacket> {

    public static final CustomPacketPayload.Type<S2CSyncAnimationExpressionPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_sync_animation_expression"));

    public static final StreamCodec<FriendlyByteBuf, S2CSyncAnimationExpressionPacket> STREAM_CODEC =
            StreamCodec.of(S2CSyncAnimationExpressionPacket::encode, S2CSyncAnimationExpressionPacket::decode);

    private final int entityId;

    private final FloatArrayList floatData;

    public S2CSyncAnimationExpressionPacket(int entityId, FloatArrayList floatData) {
        this.entityId = entityId;
        this.floatData = floatData;
    }

    public static void encode(FriendlyByteBuf buf, S2CSyncAnimationExpressionPacket message) {
        buf.writeVarInt(message.entityId);
        buf.writeByte(message.floatData.size());
        for (Float floatDatum : message.floatData) {
            buf.writeFloat(floatDatum);
        }
    }

    public static S2CSyncAnimationExpressionPacket decode(FriendlyByteBuf buf) {
        int varInt = buf.readVarInt();
        int count = buf.readByte();
        FloatArrayList floatArrayList = new FloatArrayList(count);
        for (int i = 0; i < count; i++) {
            floatArrayList.add(buf.readFloat());
        }
        return new S2CSyncAnimationExpressionPacket(varInt, floatArrayList);
    }

    @Override
    public Type<S2CSyncAnimationExpressionPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSyncAnimationExpressionPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Minecraft.getInstance().level.getEntity(payload.entityId).getCapability(PlayerCapabilityProvider.PLAYER_CAP, null).ifPresent(cap -> cap.executeAnimationExpression(payload.floatData));
            });
        }
    }
}
