package com.qovery.intellij.plugin.icon;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.qovery.intellij.plugin.util.QoveryUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.qovery.intellij.plugin.icon.Icons.QOVERY_ICON_16;

public class QoveryIconProvider extends IconProvider {

    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile != null && QoveryUtils.isQoveryFile(containingFile.getName())) {
            return QOVERY_ICON_16;
        }
        return null;
    }
}
