package com.qovery.intellij.plugin.util;

import org.apache.commons.io.FilenameUtils;

public class QoveryUtils {
    public static boolean isQoveryFile(String filename) {
        return FilenameUtils.isExtension(
                filename,
                new String[]{
                        "yaml", "yml"
                }) && filename.endsWith(".qovery.yml") || filename.endsWith(".qovery.yaml");
    }
}
