package com.qovery.intellij.plugin.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.qovery.intellij.plugin.completion.field.Field;
import com.qovery.intellij.plugin.path.PathFinder;
import com.qovery.intellij.plugin.util.SetUtil;
import com.qovery.intellij.plugin.yaml.YamlTraversal;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class QoveryCompletionHelper {

    final private PsiElement psiElement;
    final private YamlTraversal traversal;

    public QoveryCompletionHelper(PsiElement psiElement, YamlTraversal traversal) {
        this.psiElement = psiElement;
        this.traversal = traversal;
    }

    public String getProjectName() {
        return psiElement.getProject().getName();
    }

    /**
     * Return the value of the key in the YAML mapping of the psiElement.
     * example:
     * <p>
     * type: bla
     * name: <CURSOR>
     * <p>
     * getCurrentValueOfContext("type") = bla
     */
    public Optional<String> getValueOfKeyFromParentMapping(String key) {
        return traversal.getParentProperties(psiElement).stream()
                .filter(e -> e.getKeyText().equals(key))
                .map(YAMLKeyValue::getValueText)
                .map(v -> v.replace("IntellijIdeaRulezzz", ""))
                .findFirst();
    }

    public Set<String> getFilesEndingWith(String endingWith) {
        String rootDirectory = psiElement.getContainingFile().getProject().getBasePath();
        FilenameFilter filter = (dir, name) -> name.endsWith(endingWith);
        if (rootDirectory == null) {
            return SetUtil.of();
        }

        String[] listFiles = new File(rootDirectory).list(filter);
        if (listFiles == null) {
            return SetUtil.of();
        }

        return SetUtil.of(listFiles);
    }

    public boolean isUniqueKey(final String keyName) {
        List<? extends PsiNamedElement> children = new PathFinder().findDirectNamedChildren("parent", psiElement);

        return children.stream().noneMatch((c) -> keyName.equals(c.getName()));
    }

    public boolean isUniqueArrayStringValue(final String keyName) {
        return traversal.isUniqueArrayStringValue(keyName, psiElement);
    }

    public InsertHandler<LookupElement> createInsertFieldHandler(final Field field) {
        return traversal.createInsertFieldHandler(field);
    }

    public InsertHandler<LookupElement> createInsertValueHandler() {
        return traversal.createInsertValueHandler();
    }

    public boolean isRoot() {
        return psiElement.getParent() instanceof PsiFile
                || psiElement.getParent().getParent() instanceof PsiFile
                || psiElement.getParent().getParent().getParent() instanceof PsiFile
                || psiElement.getParent().getParent().getParent().getParent() instanceof PsiFile;
    }

    public boolean hasPath(final String pathExpression) {
        return new PathFinder().isInsidePath(psiElement, pathExpression);
    }
}
