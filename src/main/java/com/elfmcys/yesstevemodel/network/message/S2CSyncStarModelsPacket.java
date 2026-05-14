package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.StarModelsCapabilityProvider;
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

public class S2CSyncStarModelsPacket implements CustomPacketPayload, IPayloadHandler<S2CSyncStarModelsPacket> {

    public static final CustomPacketPayload.Type<S2CSyncStarModelsPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_sync_star_models"));

    public static final StreamCodec<FriendlyByteBuf, S2CSyncStarModelsPacket> STREAM_CODEC =
            StreamCodec.of(S2CSyncStarModelsPacket::encode, S2CSyncStarModelsPacket::decode);

    private final Set<String> starModels;

    public S2CSyncStarModelsPacket(Set<String> starModels) {
        this.starModels = starModels;
    }

    public static void encode(FriendlyByteBuf buf, S2CSyncStarModelsPacket message) {
        buf.writeVarInt(message.starModels.size());
        for (String starModel : message.starModels) {
            buf.writeUtf(starModel);
        }
    }

    public static S2CSyncStarModelsPacket decode(FriendlyByteBuf buf) {
        int varInt = buf.readVarInt();
        HashSet<String> tmp = Sets.newHashSet();
        for (int i = 0; i < varInt; i++) {
            tmp.add(buf.readUtf());
        }
        return new S2CSyncStarModelsPacket(tmp);
    }

    @Override
    public Type<S2CSyncStarModelsPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSyncStarModelsPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleCapability(payload));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleCapability(S2CSyncStarModelsPacket message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null).ifPresent(cap -> cap.setStarModels(message.starModels));
        }
    }
}
