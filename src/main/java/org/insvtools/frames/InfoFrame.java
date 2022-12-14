package org.insvtools.frames;

import java.nio.ByteBuffer;

public class InfoFrame extends Frame {
    public InfoFrame(FrameHeader header, ByteBuffer buffer) {
        super(header, buffer);
    }
}
