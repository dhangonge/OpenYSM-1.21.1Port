package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapabilityProvider;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.HashSet;
import java.util.Set;

public class S2CSyncAuthModelsPacket implements CustomPacketPayload, IPayloadHandler<S2CSyncAuthModelsPacket> {

    public static final CustomPacketPayload.Type<S2CSyncAuthModelsPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_sync_auth_models"));

    public static final StreamCodec<FriendlyByteBuf, S2CSyncAuthModelsPacket> STREAM_CODEC =
            StreamCodec.of(S2CSyncAuthModelsPacket::encode, S2CSyncAuthModelsPacket::decode);

    private final Set<String> authModels;

    public S2CSyncAuthModelsPacket(Set<String> authModels) {
        this.authModels = authModels;
    }

    public static void encode(FriendlyByteBuf buf, S2CSyncAuthModelsPacket message) {
        buf.writeVarInt(message.authModels.size());
        for (String modelId : message.authModels) {
            buf.writeUtf(modelId);
        }
    }

    public static S2CSyncAuthModelsPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        HashSet<String> tmp = Sets.newHashSet();
        for (int i = 0; i < size; i++) {
            tmp.add(buf.readUtf());
        }
        return new S2CSyncAuthModelsPacket(tmp);
    }

    @Override
    public Type<S2CSyncAuthModelsPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSyncAuthModelsPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleCapability(payload));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleCapability(S2CSyncAuthModelsPacket message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null).ifPresent(cap -> {
                cap.setAuthModels(message.authModels);
            });
        }
    }
}
