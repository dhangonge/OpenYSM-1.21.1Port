package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouMaidCompat;
import com.elfmcys.yesstevemodel.geckolib3.resource.GeckoLibCache;
import com.elfmcys.yesstevemodel.molang.parser.ParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CExecuteMolangPacket implements CustomPacketPayload, IPayloadHandler<S2CExecuteMolangPacket> {

    public static final CustomPacketPayload.Type<S2CExecuteMolangPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_execute_molang"));

    public static final StreamCodec<FriendlyByteBuf, S2CExecuteMolangPacket> STREAM_CODEC =
            StreamCodec.of(S2CExecuteMolangPacket::encode, S2CExecuteMolangPacket::decode);

    private final int[] entityIds;

    private final String expression;

    public S2CExecuteMolangPacket(int entityIds, String expression) {
        this.entityIds = new int[]{entityIds};
        this.expression = expression;
    }

    public S2CExecuteMolangPacket(int[] entityIds, String expression) {
        this.entityIds = entityIds;
        this.expression = expression;
    }

    public static void encode(FriendlyByteBuf buf, S2CExecuteMolangPacket message) {
        buf.writeVarIntArray(message.entityIds);
        buf.writeUtf(message.expression);
    }

    public static S2CExecuteMolangPacket decode(FriendlyByteBuf buf) {
        return new S2CExecuteMolangPacket(buf.readVarIntArray(), buf.readUtf());
    }

    @Override
    public Type<S2CExecuteMolangPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CExecuteMolangPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> handleCapability(payload));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleCapability(S2CExecuteMolangPacket message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        for (int i : message.entityIds) {
            Entity entity = minecraft.level.getEntity(i);
            if (entity instanceof Player) {
                PlayerCapability cap = entity.getCapability(PlayerCapabilityProvider.PLAYER_CAP, null);
                if (cap != null) {
                    try {
                        cap.executeExpression(GeckoLibCache.parseSimpleExpression(message.expression), true, false, null);
                    } catch (ParseException e) {
                        YesSteveModel.LOGGER.error("Failed to execute molang " + message.expression, e);
                    }
                }
            } else if (TouhouMaidCompat.isMaidEntity(entity)) {
                TouhouMaidCompat.playMaidAnimation(entity, message.expression);
            }
        }
    }
}
