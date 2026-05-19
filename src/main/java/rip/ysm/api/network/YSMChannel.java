package rip.ysm.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class YSMChannel {

    private YSMChannel() {
    }

    public static void init(ResourceLocation channelId, String version) {
        throw new AssertionError();
    }

    public static <T> void register(int discriminator, Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, PacketContext> handler, PacketDirection direction) {
        throw new AssertionError();
    }

    public static void sendToServer(Object packet) {
        throw new AssertionError();
    }

    public static void sendToClientPlayer(Object packet, ServerPlayer player) {
        throw new AssertionError();
    }

    public static void sendToAll(Object packet) {
        throw new AssertionError();
    }

    public static void sendToTrackingEntity(Object packet, Entity entity) {
        throw new AssertionError();
    }

    public static void sendToTrackingEntityAndSelf(Object packet, Player player) {
        throw new AssertionError();
    }

    public static Packet<?> toClientboundPacket(Object packet) {
        throw new AssertionError();
    }

    public static Packet<?> toServerboundPacket(Object packet) {
        throw new AssertionError();
    }
}
