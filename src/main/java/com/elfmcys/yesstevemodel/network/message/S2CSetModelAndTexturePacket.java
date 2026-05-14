package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.event.EntityJoinCallbackEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CSetModelAndTexturePacket implements CustomPacketPayload, IPayloadHandler<S2CSetModelAndTexturePacket> {

    public static final CustomPacketPayload.Type<S2CSetModelAndTexturePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_set_model_and_texture"));

    public static final StreamCodec<FriendlyByteBuf, S2CSetModelAndTexturePacket> STREAM_CODEC =
            StreamCodec.of(S2CSetModelAndTexturePacket::encode, S2CSetModelAndTexturePacket::decode);

    private final int entityId;

    private final String modelId;

    private final String textureId;

    private final boolean disabled;

    private final S2CSyncPlayerStatePacket entityModelSync;

    public S2CSetModelAndTexturePacket(int entityId, String modelId, String textureId, boolean disabled, S2CSyncPlayerStatePacket playerState) {
        this.entityId = entityId;
        this.modelId = modelId;
        this.textureId = textureId;
        this.entityModelSync = playerState;
        this.disabled = disabled;
    }

    public static void encode(FriendlyByteBuf buf, S2CSetModelAndTexturePacket other) {
        buf.writeVarInt(other.entityId);
        buf.writeUtf(other.modelId);
        buf.writeUtf(other.textureId);
        buf.writeBoolean(other.disabled);
        S2CSyncPlayerStatePacket.encode(buf, other.entityModelSync);
    }

    public static S2CSetModelAndTexturePacket decode(FriendlyByteBuf buf) {
        return new S2CSetModelAndTexturePacket(buf.readVarInt(), buf.readUtf(), buf.readUtf(), buf.readBoolean(), S2CSyncPlayerStatePacket.decode(buf));
    }

    @Override
    public Type<S2CSetModelAndTexturePacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSetModelAndTexturePacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            EntityJoinCallbackEvent.addCallback(payload.entityId, entity -> {
                applyOnClient(entity, payload);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void applyOnClient(Entity entity, S2CSetModelAndTexturePacket other) {
        PlayerCapability cap = entity.getCapability(PlayerCapabilityProvider.PLAYER_CAP, null);
        if (cap != null) {
            cap.initModelWithTexture(other.modelId, other.textureId);
            cap.setForceDisabled(other.disabled);
            S2CSyncPlayerStatePacket.handleCapability(entity, other.entityModelSync);
        }
    }
}
