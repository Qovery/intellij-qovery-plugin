package com.qovery.intellij.plugin.util;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;

public class QoveryUtils {
    public static boolean isQoveryFile(VirtualFile file) {
        return FilenameUtils.isExtension(
                file.getName(),
                new String[]{
                        "yaml", "yml"
                }) && file.getName().endsWith(".qovery.yml");
    }
}
