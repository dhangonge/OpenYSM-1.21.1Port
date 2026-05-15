package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.resource.models.ModelProperties;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.elfmcys.yesstevemodel.util.data.OrderedStringMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class C2SPlayAnimationPacket implements CustomPacketPayload, IPayloadHandler<C2SPlayAnimationPacket> {

    public static final CustomPacketPayload.Type<C2SPlayAnimationPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "c2s_play_animation"));

    public static final StreamCodec<FriendlyByteBuf, C2SPlayAnimationPacket> STREAM_CODEC =
            StreamCodec.of(C2SPlayAnimationPacket::encode, C2SPlayAnimationPacket::decode);

    private final int animationIndex;

    private final String category;

    private final int entityId;

    public C2SPlayAnimationPacket(int animationIndex, String category, int entityId) {
        this.animationIndex = animationIndex;
        this.category = category;
        this.entityId = entityId;
    }

    public C2SPlayAnimationPacket(int animationIndex, String category) {
        this(animationIndex, category, -1);
    }

    public static C2SPlayAnimationPacket createDefault() {
        return new C2SPlayAnimationPacket(-1, StringPool.EMPTY);
    }

    public static C2SPlayAnimationPacket createWithIndex(int entityId) {
        return new C2SPlayAnimationPacket(-1, StringPool.EMPTY, entityId);
    }

    public static void encode(FriendlyByteBuf buf, C2SPlayAnimationPacket message) {
        buf.writeVarInt(message.animationIndex);
        buf.writeUtf(message.category);
        buf.writeVarInt(message.entityId);
    }

    public static C2SPlayAnimationPacket decode(FriendlyByteBuf buf) {
        return new C2SPlayAnimationPacket(buf.readVarInt(), buf.readUtf(), buf.readVarInt());
    }

    @Override
    public Type<C2SPlayAnimationPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(C2SPlayAnimationPacket payload, IPayloadContext context) {
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

    private static void handleCapability(C2SPlayAnimationPacket message, ServerPlayer sender) {
        if (message.entityId != -1) {
            Entity entity = sender.serverLevel().getEntity(message.entityId);
            if (TouhouMaidCompat.isMaidEntity(entity)) {
                TouhouMaidCompat.registerAnimationRoulette(entity, message.category, message.animationIndex);
                return;
            }
            return;
        }

        ModelInfoCapability modelInfoCap = sender.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
        if (modelInfoCap != null) {
            if (message.animationIndex == -1) {
                modelInfoCap.stopAnimation(sender);
            } else {
                ServerModelManager.getModelDefinition(modelInfoCap.getModelId()).ifPresent(serverModelCap -> {
                    OrderedStringMap<String, String> extraAnimations;
                    ModelProperties modelProperties = serverModelCap.getLoadedModelData().getModelProperties();
                    Map<String, OrderedStringMap<String, String>> extraAnimationClassify = modelProperties.getExtraAnimationClassify();
                    if (StringUtils.isNotBlank(message.category) && extraAnimationClassify.containsKey(message.category)) {
                        extraAnimations = extraAnimationClassify.get(message.category);
                    } else {
                        extraAnimations = modelProperties.getExtraAnimation();
                    }
                    if (extraAnimations.size() > message.animationIndex) {
                        modelInfoCap.playAnimation(sender, extraAnimations.getKeyAt(message.animationIndex));
                    }
                });
            }
        }
    }
}
