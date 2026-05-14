package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.ClientLazyCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ProjectileCapability;
import com.elfmcys.yesstevemodel.capability.ProjectileModelCapability;
import com.elfmcys.yesstevemodel.event.EntityJoinCallbackEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class S2CSyncProjectileModelPacket implements CustomPacketPayload, IPayloadHandler<S2CSyncProjectileModelPacket> {

    public static final CustomPacketPayload.Type<S2CSyncProjectileModelPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_sync_projectile_model"));

    public static final StreamCodec<FriendlyByteBuf, S2CSyncProjectileModelPacket> STREAM_CODEC =
            StreamCodec.of(S2CSyncProjectileModelPacket::encode, S2CSyncProjectileModelPacket::decode);

    private final int entityId;

    private final ProjectileModelCapability capability;

    private final Int2FloatOpenHashMap floatMap;

    public S2CSyncProjectileModelPacket(int entityId, ProjectileModelCapability capability, Int2FloatOpenHashMap floatMap) {
        this.entityId = entityId;
        this.capability = capability;
        this.floatMap = floatMap;
    }

    public S2CSyncProjectileModelPacket(int entityId, ProjectileModelCapability capability) {
        this(entityId, capability, new Int2FloatOpenHashMap());
    }

    public static void encode(FriendlyByteBuf buf, S2CSyncProjectileModelPacket message) {
        buf.writeVarInt(message.entityId);
        buf.writeNbt(message.capability.serializeNBT());
    }

    public static S2CSyncProjectileModelPacket decode(FriendlyByteBuf buf) {
        int varInt = buf.readVarInt();
        CompoundTag nbt = buf.readNbt();
        ProjectileModelCapability cap = new ProjectileModelCapability();
        if (nbt != null) {
            cap.deserializeNBT(nbt);
        }
        Object2FloatOpenHashMap<String> objectMap = cap.getMolangVars();
        Int2FloatOpenHashMap floatMap = new Int2FloatOpenHashMap();
        objectMap.object2FloatEntrySet().fastForEach(entry -> floatMap.put(StringPool.computeIfAbsent(entry.getKey()), entry.getFloatValue()));
        return new S2CSyncProjectileModelPacket(varInt, cap, floatMap);
    }

    @Override
    public Type<S2CSyncProjectileModelPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSyncProjectileModelPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            EntityJoinCallbackEvent.addCallback(payload.entityId, entity -> handleCapability(entity, payload.capability, payload.floatMap));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleCapability(Entity entity, ProjectileModelCapability capability, Int2FloatOpenHashMap floatMap) {
        entity.getCapability(ClientLazyCapabilityProvider.CLIENT_LAZY_CAP, null).ifPresent(cap -> {
            ProjectileCapability projectileCapability = cap.getProjectileAnimProvider().getOrCreateCapability();
            projectileCapability.updateModelId(capability.getOwnerModelId());
            projectileCapability.setFloatProperties(floatMap);
        });
    }
}
