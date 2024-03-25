package org.insvtools;

import org.mp4parser.PropertyBoxParserImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class tries to reach all possible reflection calls and access all available resources to gather data
 * by java-agent and generate files required for native image building.
 */
public class InsvToolsAgent {
    private static Set<Class<?>> findAllClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("\\.", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + '.'
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String insvFileName = InsvToolsAgent.class.getClassLoader().getResource("sample.insv").getFile();
        try {
            InsvTools.run("cut", "--end-time=1", insvFileName);
        }
        finally {
            Files.deleteIfExists(new File("sample.cut.insv").toPath());
        }

        try {
            InsvTools.run("dump-meta", insvFileName);
        }
        finally {
            Files.deleteIfExists(new File("sample.insv.meta.json").toPath());
        }

        // Fields and methods accessed by reflection for JSON generation.
        findAllClasses("org.insvtools.frames").forEach(Class::getDeclaredFields);
        findAllClasses("org.insvtools.frames").forEach(Class::getDeclaredMethods);
        findAllClasses("org.insvtools.records").forEach(Class::getDeclaredFields);
        findAllClasses("org.insvtools.records").forEach(Class::getDeclaredMethods);

        // Constructors for atoms accessed by reflection.
        PropertyBoxParserImpl parser = new PropertyBoxParserImpl();
        for (String type : parser.mapping.stringPropertyNames()) {
            try {
                parser.createBox(type, null, null);
            }
            catch (Throwable ignore) {
            }
        }
    }
}