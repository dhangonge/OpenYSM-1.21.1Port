package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class C2SRequestExecuteMolangPacket implements CustomPacketPayload, IPayloadHandler<C2SRequestExecuteMolangPacket> {

    public static final CustomPacketPayload.Type<C2SRequestExecuteMolangPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_request_execute_molang"));

    public static final StreamCodec<FriendlyByteBuf, C2SRequestExecuteMolangPacket> STREAM_CODEC =
            StreamCodec.of(C2SRequestExecuteMolangPacket::encode, C2SRequestExecuteMolangPacket::decode);

    private final String animationName;

    private final int entityId;

    public C2SRequestExecuteMolangPacket(String str, int i) {
        this.animationName = str;
        this.entityId = i;
    }

    public static void encode(FriendlyByteBuf buf, C2SRequestExecuteMolangPacket message) {
        buf.writeUtf(message.animationName);
        buf.writeVarInt(message.entityId);
    }

    public static C2SRequestExecuteMolangPacket decode(FriendlyByteBuf buf) {
        return new C2SRequestExecuteMolangPacket(buf.readUtf(), buf.readVarInt());
    }

    @Override
    public Type<C2SRequestExecuteMolangPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SRequestExecuteMolangPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> handleOnServer(payload, context));
        }
    }

    public static void handleOnServer(C2SRequestExecuteMolangPacket message, IPayloadContext context) {
        Entity entity;
        ServerPlayer sender = (ServerPlayer) context.player();
        if (sender == null || !sender.isAlive() || (entity = sender.level().getEntity(message.entityId)) == null) {
            return;
        }
        NetworkHandler.sendToTrackingEntity(new S2CExecuteMolangPacket(message.entityId, message.animationName), entity);
    }
}
