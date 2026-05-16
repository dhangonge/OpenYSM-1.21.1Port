package com.elfmcys.yesstevemodel.audio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class OggVorbisAudioStream implements IAudioStreamSupport {

    private static final ByteBuffer EMPTY_BUFFER = BufferUtils.createByteBuffer(0);

    private final long decoder;

    private final AudioFormat audioFormat;

    private final int channels;

    @Nullable
    private final AudioCacheBuilder cacheBuilder;

    private final ByteBuffer inputBuffer;

    private volatile boolean isClosed;

    private boolean isEndOfStream;

    public OggVorbisAudioStream(ByteBuffer byteBuffer, @Nullable AudioCacheBuilder cacheBuilder) throws UnsupportedAudioFileException, IOException {
        this.cacheBuilder = cacheBuilder;

        ByteBuffer directBuffer;
        if (byteBuffer.isDirect()) {
            directBuffer = byteBuffer;
        } else {
            directBuffer = ByteBuffer.allocateDirect(byteBuffer.remaining());
            directBuffer.put(byteBuffer.duplicate());
            directBuffer.flip();
        }
        this.inputBuffer = directBuffer;

        IntBuffer error = BufferUtils.createIntBuffer(1);
        this.decoder = STBVorbis.stb_vorbis_open_memory(directBuffer, error, null);
        if (this.decoder == MemoryUtil.NULL) {
            throw new UnsupportedAudioFileException("STBVorbis failed to open stream: error " + error.get(0));
        }

        STBVorbisInfo info = STBVorbisInfo.malloc();
        float sampleRate;
        try {
            STBVorbis.stb_vorbis_get_info(this.decoder, info);
            this.channels = info.channels();
            sampleRate = info.sample_rate();
        } finally {
            info.free();
        }

        if (this.channels != 1 && this.channels != 2) {
            STBVorbis.stb_vorbis_close(this.decoder);
            throw new UnsupportedAudioFileException("Unsupported number of channels: " + this.channels);
        }

        this.audioFormat = new AudioFormat(sampleRate, 16, 1, true, false);
    }

    @NotNull
    public AudioFormat getFormat() {
        return this.audioFormat;
    }

    @NotNull
    public ByteBuffer read(int size) throws IOException {
        if (this.isEndOfStream || this.isClosed) {
            return EMPTY_BUFFER;
        }

        int monoSamples = size / 2;
        if (monoSamples <= 0) {
            return EMPTY_BUFFER;
        }

        int interleavedCapacity = monoSamples * this.channels;
        ShortBuffer pcm = BufferUtils.createShortBuffer(interleavedCapacity);
        int samplesDecoded = STBVorbis.stb_vorbis_get_samples_short_interleaved(this.decoder, this.channels, pcm);

        if (samplesDecoded <= 0) {
            if (this.cacheBuilder != null) {
                this.cacheBuilder.flushToCache();
            }
            this.isEndOfStream = true;
            return EMPTY_BUFFER;
        }

        ByteBuffer output = BufferUtils.createByteBuffer(samplesDecoded * 2);
        output.order(ByteOrder.nativeOrder());

        if (this.channels == 2) {
            pcm.position(0);
            for (int i = 0; i < samplesDecoded; i++) {
                short left = pcm.get(i * 2);
                short right = pcm.get(i * 2 + 1);
                short mixed = (short) Math.round((left + right) / 2.0f);
                output.putShort(mixed);
            }
        } else {
            pcm.position(0);
            pcm.limit(samplesDecoded);
            for (int i = 0; i < samplesDecoded; i++) {
                output.putShort(pcm.get(i));
            }
        }
        output.flip();

        if (this.cacheBuilder != null) {
            this.cacheBuilder.appendAudio(output.duplicate());
        }

        return output;
    }

    public void close() throws IOException {
        if (!this.isClosed) {
            STBVorbis.stb_vorbis_close(this.decoder);
            this.isClosed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
}