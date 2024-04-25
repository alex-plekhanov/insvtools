package org.insvtools.frames;

public enum FrameType {
    RAW(-1),
    INDEX(0),
    INFO(1),
    THUMBNAIL(2),
    GYRO(3),
    EXPOSURE(4),
    THUMBNAIL_EXT(5),
    TIMELAPSE(6),
    GPS(7),
    STAR_NUM(8),
    THREE_A_IN_TIMESTAMP(9),
    ANCHORS(10),
    THREE_A_SIMULATION(11),
    EXPOSURE_SECONDARY(12),
    MAGNETIC(13),
    EULER(14),
    GYRO_SECONDARY(15),
    SPEED(16),
    TBOX(17),
    EDITOR(18),
    HEARTRATE(19),
    FORWARD_DIRECTION(20),
    UPVIEW(21),
    SHELL_RECOGNITION_DATA(22),
    POS(23),
    TIMELAPSE_QUAT(24);
    //TIME_MAP(-128);

    private final byte code;

    FrameType(int code) {
        this.code = (byte)code;
    }

    public byte getCode() {
        return code;
    }

    public static FrameType valueOf(byte code) {
        for (FrameType type : values())
            if (type.code == code)
                return type;

        return null;
    }
}
