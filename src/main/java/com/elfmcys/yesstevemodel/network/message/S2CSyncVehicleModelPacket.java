package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.ClientLazyCapability;
import com.elfmcys.yesstevemodel.capability.ClientLazyCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.VehicleModelCapability;
import com.elfmcys.yesstevemodel.event.EntityJoinCallbackEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.elfmcys.yesstevemodel.capability.VehicleCapability;
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

public class S2CSyncVehicleModelPacket implements CustomPacketPayload, IPayloadHandler<S2CSyncVehicleModelPacket> {

    public static final CustomPacketPayload.Type<S2CSyncVehicleModelPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "s2c_sync_vehicle_model"));

    public static final StreamCodec<FriendlyByteBuf, S2CSyncVehicleModelPacket> STREAM_CODEC =
            StreamCodec.of(S2CSyncVehicleModelPacket::encode, S2CSyncVehicleModelPacket::decode);

    private final int entityId;

    private final VehicleModelCapability capability;

    private final Int2FloatOpenHashMap floatMap;

    public S2CSyncVehicleModelPacket(int entityId, VehicleModelCapability capability, Int2FloatOpenHashMap floatMap) {
        this.entityId = entityId;
        this.capability = capability;
        this.floatMap = floatMap;
    }

    public S2CSyncVehicleModelPacket(int entityId, VehicleModelCapability capability) {
        this(entityId, capability, new Int2FloatOpenHashMap(0));
    }

    public static void encode(FriendlyByteBuf buf, S2CSyncVehicleModelPacket message) {
        buf.writeVarInt(message.entityId);
        buf.writeNbt(message.capability.serializeNBT());
    }

    public static S2CSyncVehicleModelPacket decode(FriendlyByteBuf buf) {
        int varInt = buf.readVarInt();
        CompoundTag nbt = buf.readNbt();
        VehicleModelCapability cap = new VehicleModelCapability();
        if (nbt != null) {
            cap.deserializeNBT(nbt);
        }
        Object2FloatOpenHashMap<String> objectMap = cap.getMolangVars();
        Int2FloatOpenHashMap floatMap = new Int2FloatOpenHashMap();
        objectMap.object2FloatEntrySet().fastForEach(entry -> floatMap.put(StringPool.computeIfAbsent(entry.getKey()), entry.getFloatValue()));
        return new S2CSyncVehicleModelPacket(varInt, cap, floatMap);
    }

    @Override
    public Type<S2CSyncVehicleModelPacket> type() {
        return TYPE;
    }

    @Override
    public void handle(S2CSyncVehicleModelPacket payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            EntityJoinCallbackEvent.addCallback(payload.entityId, entity -> handleCapability(entity, payload.capability, payload.floatMap));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleCapability(Entity entity, VehicleModelCapability capability, Int2FloatOpenHashMap floatMap) {
        ClientLazyCapability cap = entity.getCapability(ClientLazyCapabilityProvider.CLIENT_LAZY_CAP, null);
        if (cap != null) {
            VehicleCapability vehicleCapability = cap.getEntityRenderCapability();
            vehicleCapability.setOwnerModelId(capability.getOwnerModelId());
            vehicleCapability.setFloatMap(floatMap);
        }
    }
}
