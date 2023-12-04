package org.insvtools.commands;

import junit.framework.TestCase;

import java.io.File;

public class CutCommandTest extends TestCase {
    public void testCutFileFor() {
        // Test empty cut-file parameter.
        CutCommand cutCommand = cutCommand("VID_20231203_220002_00_117.insv", null);
        assertEquals("LRV_20231203_220002_00_117.cut.insv",
                cutCommand.cutFileFor(new File("LRV_20231203_220002_00_117.insv")).getName());
        assertEquals("VID_20231203_220002_00_117.cut.insv",
                cutCommand.cutFileFor(new File("VID_20231203_220002_00_117.insv")).getName());
        assertEquals("VID_20231203_220002_10_117.cut.insv",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.insv")).getName());
        assertEquals("VID_20231203_220002_10_117.cut.mp4",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.mp4")).getName());

        // Test non-empty cut-file parameter, matching main file name.
        cutCommand = cutCommand("VID_20231203_220002_00_117.insv", "CUT_VID_20231203_220002_00_117.CUT");
        assertEquals("CUT_LRV_20231203_220002_00_117.CUT",
                cutCommand.cutFileFor(new File("LRV_20231203_220002_00_117.insv")).getName());
        assertEquals("CUT_VID_20231203_220002_00_117.CUT",
                cutCommand.cutFileFor(new File("VID_20231203_220002_00_117.insv")).getName());
        assertEquals("CUT_VID_20231203_220002_10_117.CUT",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.insv")).getName());
        assertEquals("CUT_VID_20231203_220002_10_117.CUT",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.mp4")).getName());

        // Test non-empty cut-file parameter, not matching main file name.
        cutCommand = cutCommand("VID_20231203_220002_00_117.insv", "cut_file.insv");
        assertEquals("LRV_20231203_220002_00_117.cut.insv",
                cutCommand.cutFileFor(new File("LRV_20231203_220002_00_117.insv")).getName());
        assertEquals("cut_file.insv",
                cutCommand.cutFileFor(new File("VID_20231203_220002_00_117.insv")).getName());
        assertEquals("VID_20231203_220002_10_117.cut.insv",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.insv")).getName());
        assertEquals("VID_20231203_220002_10_117.cut.mp4",
                cutCommand.cutFileFor(new File("VID_20231203_220002_10_117.mp4")).getName());
    }

    private static CutCommand cutCommand(String fileName, String cutFileName) {
        return new CutCommand(fileName, cutFileName, 0, 0, 0, false);
    }
}