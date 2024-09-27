package org.insvtools.commands;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        assertEquals("LRV_20231203_220002_10_117.cut.lrv",
                cutCommand.cutFileFor(new File("LRV_20231203_220002_10_117.lrv")).getName());

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

    public void testFilesToProcess() {
        Map<File, File> filesToProcess = filesToProcess("VID_20231203_220002_00_117.insv",
                "VID_20231203_220002_00_117.insv",
                "VID_20231203_220002_01_117.insv",
                "LRV_20231203_220002_00_117.insv",
                "TST_20231203_220002_00_117.insv",
                "VID_20231203_220002_00_118.insv",
                "LRV_20231203_220002_00_118.insv"
        );

        assertFiles(filesToProcess,
                "VID_20231203_220002_00_117.insv",
                "VID_20231203_220002_01_117.insv",
                "LRV_20231203_220002_00_117.insv"
        );

        filesToProcess = filesToProcess("VID_20231203_220002_01_117.insv",
                "VID_20231203_220002_00_117.insv",
                "VID_20231203_220002_01_117.insv",
                "LRV_20231203_220002_00_117.insv",
                "TST_20231203_220002_00_117.insv",
                "VID_20231203_220002_00_118.insv",
                "LRV_20231203_220002_00_118.insv"
        );

        assertFiles(filesToProcess,
                "VID_20231203_220002_00_117.insv",
                "VID_20231203_220002_01_117.insv",
                "LRV_20231203_220002_00_117.insv"
        );

        filesToProcess = filesToProcess("VID_20240414_135511_00_027.insv",
                "VID_20240414_135511_00_027.insv",
                "VID_20240414_135511_00_028.insv",
                "VID_20240414_135511_00_027.mp4",
                "LRV_20240414_135511_01_027.lrv"
        );

        assertFiles(filesToProcess,
                "VID_20240414_135511_00_027.insv",
                "LRV_20240414_135511_01_027.lrv"
        );

        filesToProcess = filesToProcess("PRO_VID_20221010_115706_00_002.mp4",
                "PRO_VID_20221010_115706_00_002.mp4",
                "PRO_LRV_20221010_115706_01_002.mp4",
                "PRO_LRV_20221010_115706_01_002.insv",
                "PRO_VID_20221010_115706_00_003.mp4",
                "VID_20221010_115706_00_002.mp4",
                "LRV_20221010_115706_01_002.mp4"
        );

        assertFiles(filesToProcess,
                "PRO_VID_20221010_115706_00_002.mp4",
                "PRO_LRV_20221010_115706_01_002.mp4"
        );
    }

    private Map<File, File> filesToProcess(String mainFile, String... filesList) {
        return new CutCommand(mainFile, null, 0, 0, 0, true) {
            @Override File[] listFiles(FileFilter filter) {
                return Arrays.stream(filesList).map(File::new).filter(filter::accept).toArray(File[]::new);
            }
        }.filesToProcess();
    }

    private void assertFiles(Map<File, File> filesToProcess, String... fileNamesExpected) {
        Set<String> fileNames = filesToProcess.keySet().stream().map(File::getName).collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList(fileNamesExpected)), fileNames);
    }

    private static CutCommand cutCommand(String fileName, String cutFileName) {
        return new CutCommand(fileName, cutFileName, 0, 0, 0, false);
    }
}