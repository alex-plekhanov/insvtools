package org.insvtools.commands;

import junit.framework.TestCase;
import org.insvtools.InsvTools;
import org.insvtools.InsvToolsAgent;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;

public class SampleFileTest extends TestCase {
    public void testSampleFile() throws Exception {
        String insvFileName = InsvToolsAgent.class.getClassLoader().getResource("sample.insv").getFile();
        try {
            Assert.assertEquals(0, InsvTools.run("cut", "--end-time=1", insvFileName));
        }
        finally {
            Files.deleteIfExists(new File("sample.cut.insv").toPath());
        }

        try {
            Assert.assertEquals(0, InsvTools.run("dump-meta", insvFileName));
        }
        finally {
            Files.deleteIfExists(new File("sample.insv.meta.json").toPath());
        }
    }
}