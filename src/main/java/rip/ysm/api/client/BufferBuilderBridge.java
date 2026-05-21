package rip.ysm.api.client;

import com.mojang.blaze3d.vertex.BufferBuilder;

import java.nio.ByteBuffer;

public final class BufferBuilderBridge {

    private BufferBuilderBridge() {
    }

    public static boolean putBulkData(BufferBuilder builder, ByteBuffer buffer) {
        throw new AssertionError();
    }

    public static boolean supportsDirectTransfer() {
        throw new AssertionError();
    }
}
