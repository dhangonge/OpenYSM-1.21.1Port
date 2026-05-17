package org.gagravarr.ogg.audio;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.gagravarr.ogg.OggPacketReader;
import org.gagravarr.ogg.OggStreamAudioData;
import org.gagravarr.opus.OpusFile;

/**
 * Bridge class for code that still references the old Object class
 * which was renamed to OggAudioStream in newer versions.
 */
public class StreamAudioObject implements OggAudioStream {

    public static class Format {
        private final int channels;
        private final float sampleRate;

        public Format(int channels, float sampleRate) {
            this.channels = channels;
            this.sampleRate = sampleRate;
        }

        public int getChannels() {
            return channels;
        }

        public float getSampleRate() {
            return sampleRate;
        }
    }

    private final Format format;
    private final InputStream inputStream;
    private boolean closed;

    public StreamAudioObject(InputStream inputStream) {
        this.inputStream = inputStream;
        this.format = new Format(2, 44100.0f);
    }

    public StreamAudioObject(OpusFile warp) {
        OggPacketReader r = warp.getOggFile().getPacketReader();

        try {
           Field inp = r.getClass().getDeclaredField("inp");
           inp.setAccessible(true);
           this.inputStream = (InputStream) inp.get(r);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.format = new Format(2, 44100.0f);
    }

    public Format getFormat() {
        return format;
    }

    public ByteBuffer read(int bytes) throws IOException {
        byte[] buf = new byte[bytes];
        int read = inputStream.read(buf);
        if (read < 0) {
            return ByteBuffer.allocate(0);
        }
        return ByteBuffer.wrap(buf, 0, read);
    }

    public void close() throws IOException {
        closed = true;
        inputStream.close();
    }

    @Override
    public OggStreamAudioData getNextAudioPacket() throws IOException {
        return null;
    }

    @Override
    public void skipToGranule(long granulePosition) throws IOException {
    }
}
