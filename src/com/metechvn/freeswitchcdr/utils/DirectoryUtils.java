package com.metechvn.freeswitchcdr.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryUtils {

    private DirectoryUtils() {}

    public static Path workingPath() {
        var currentRelativePath = Paths.get("");

        return currentRelativePath.toAbsolutePath();
    }

}
