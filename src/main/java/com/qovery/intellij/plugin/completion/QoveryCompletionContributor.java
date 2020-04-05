package com.qovery.intellij.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.qovery.intellij.plugin.completion.field.FieldCompletionFiller;
import com.qovery.intellij.plugin.completion.value.ValueSet;
import com.qovery.intellij.plugin.completion.value.ValueSetCompletionFiller;
import com.qovery.intellij.plugin.util.QoveryUtils;
import com.qovery.intellij.plugin.yaml.YamlTraversal;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class QoveryCompletionContributor extends CompletionContributor {

    private final Spec spec = new Spec();

    private final YamlTraversal yamlTraversal;

    public QoveryCompletionContributor(final YamlTraversal yamlTraversal) {
        this.yamlTraversal = yamlTraversal;
    }

    public QoveryCompletionContributor() {
        this(new YamlTraversal());
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        final VirtualFile virtualFile = parameters.getOriginalFile().getVirtualFile();

        if (QoveryUtils.isQoveryFile(virtualFile.getName())) {

            final PsiElement psiElement = parameters.getPosition();

            final QoveryCompletionHelper completionHelper = new QoveryCompletionHelper(
                    psiElement,
                    this.yamlTraversal
            );

            completionHelper.getValueOfKeyFromParentMapping("type").ifPresent(System.out::println);

            fillFieldsCompletion(completionHelper, result, spec);
            fillValuesCompletion(completionHelper, result, spec);

            result.stopHere();
        }

    }

    private void fillFieldsCompletion(final QoveryCompletionHelper completionHelper,
                                      final CompletionResultSet completionResultSet, final Spec spec) {
        spec.fields.forEach((path, field) -> {
            if ((path.equals("$.") && completionHelper.isRoot()) || completionHelper.hasPath(path)) {
                FieldCompletionFiller.fill(completionHelper, completionResultSet, field);
            }
        });
    }

    private void fillValuesCompletion(final QoveryCompletionHelper completionHelper,
                                      final CompletionResultSet completionResultSet, final Spec spec) {
        spec.values.forEach((path, valueSetList) -> {
            if ((path.equals("$.") && completionHelper.isRoot()) || completionHelper.hasPath(path)) {
                List<ValueSet> nonContextualValueSet = valueSetList.stream()
                        .filter(e -> !e.isContextual())
                        .collect(Collectors.toList());

                ValueSetCompletionFiller.fill(completionHelper, completionResultSet, nonContextualValueSet);

                List<ValueSet> contextualValueSet = valueSetList.stream().filter(ValueSet::isContextual)
                        .map(valueSet -> valueSet.fromContext(completionHelper))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                ValueSetCompletionFiller.fill(completionHelper, completionResultSet, contextualValueSet);
            }
        });
    }
}
