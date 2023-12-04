
# InsvTools

A toolkit for working with Insta360 camera video files (*.insv).  

## Building from source

### Build a jar

Maven and JDK 8 or later are required to build InsvTools as a jar.

Сommand:
```
mvn clean package -DskipTests
```
Produces archive `target/release-package/insvtools-${version}.zip` with jar file and all dependencies.

### Build GraalVM native image

Maven and GraalVM with native-image tool (including dependencies, see https://www.graalvm.org/latest/reference-manual/native-image/#prerequisites) are required to build InsvTools native image.

Commands:
```
mvn clean test-compile
mvn exec:exec@java-agent -Pnative -Dagent
mvn package -Pnative -Dagent -DskipTests 
```
Produces archive `target/release-package/insvtools-native-${version}.zip` with standalone executable file `insvtools`.

## Using InsvTools

It's assumed in this section that you are using native image to run InsvTools. If you are using jar, just replace `insvtools` with `java -jar insvtools.jar` in the following examples.

### Cut *.insv files
The InsvTools can be used to cut the video files based on start time and/or end time.

Command line to cut video files: 
```
insvtools cut [--start-time=<time>] [--end-time=<time>] [--group=<true/false>] [--out-file=<filename>] <filename>
```
Parameters:

| Parameter                     | Description                                                                                 |
|-------------------------------|---------------------------------------------------------------------------------------------|
| `[--start-time=<time>]`       | Start time in format [MM:]SS[.SSS]                                                          |
| `[--end-time=<time>]`         | End time in format [MM:]SS[.SSS]                                                            |
| `[--timestamp-scale=<scale>]` | Gyro records timestamp scale (autodetect by default)                                        |
| `[--group=<true/false>]`      | Process the whole group of files related to specified file (true by default)                |
| `[--out-file=<filename>]`     | Use specified output file (by default 'cut' suffix will be added to the original file name) |
| `<filename>`                  | Input *.insv file name                                                                      |

### Examples

For example, we have group of files related to the same video: `VID_20221218_231825_00_677.insv`, `VID_20221218_231825_10_677.insv` and `LRV_20221218_231825_11_677.insv`.

To cut clip starting from 10 seconds 500 milliseconds and ending at 100 seconds 125 milliseconds we can use command line:
```
insvtools cut --start-time=10.500 --end-time=100.125 VID_20221218_231825_00_677.insv
```

All three files will be processed and new files: `VID_20221218_231825_00_677.cut.insv`, `VID_20221218_231825_10_677.cut.insv` and `LRV_20221218_231825_11_677.cut.insv` will be created. If, for some reason, we need to process only one of these files, we can specify `--group false` parameter:
```
insvtools cut --start-time=10.500 --end-time=100.125 --group=false VID_20221218_231825_00_677.insv
```

To cut clip starting from 10 seconds 500 milliseconds till the end of the file, we can specify only `--start-time` parameter:
```
insvtools cut --start-time=10.500 VID_20221218_231825_00_677.insv
```

Similarly, to cut clip from the beginning and ending at some point in time, we can specify only  `--end-time` parameter:
```
insvtools cut --end-time=1:40.124 VID_20221218_231825_00_677.insv
```

### Other commands

The `insvtools` also has a set of other commands, but these commands will be helpful mostly for *.insv file format explorers and shouldn't be interested by end users.

Here is usage help for other commands:
```
insvtools <cmd> [parameters] <filename>

Commands and parameters:

    dump-meta                       - dump insv file metadata
        [--frame-type=<frame-type>] - dump only specified integer frame type (by default all frames will be dumped)
        [--dump-file=<filename>]    - dump file name (by default 'meta.json' suffix will be added to the original file name)
    
    decompose-meta                  - store insv file metadata to file-per-frame
        [--frame-type=<frame-type>] - store only specified integer frame type (by default all frames will be stored)
    
    compose-meta                    - compose file-per-frame metadata to the insv file
    
    remove-meta                     - remove insv file metadata
    
    extract-meta                    - extract insv file metadata as one file
        [--meta-file=<filename>]    - metadata file name (by default 'meta' suffix will be added to the original file name)
    
    replace-meta                    - replace insv file metadata with another from file
        [--meta-file=<filename>]    - metadata file name (by default 'meta' suffix will be added to the original file name)
```

