/*
The MIT License (MIT) Copyright © 2016 Zalando SE, https://tech.zalando.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the “Software”), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of
the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.qovery.intellij.plugin.yaml;

import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.qovery.intellij.plugin.completion.field.Field;
import com.qovery.intellij.plugin.util.StringUtils;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLSequence;
import org.jetbrains.yaml.psi.YAMLSequenceItem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class YamlTraversal {

    public boolean isUniqueArrayStringValue(final String value, final PsiElement psiElement) {
        return Optional.ofNullable(psiElement.getParent())
                .map(PsiElement::getParent)
                .map(PsiElement::getParent)
                .filter(el -> el instanceof YAMLSequence)
                .map(el -> Arrays.asList(el.getChildren()))
                .map(children -> children.stream().filter(c -> c instanceof YAMLSequenceItem))
                .map(childrenStream -> childrenStream.map(YAMLSequenceItem.class::cast))
                .map(
                        childrenStream ->
                                childrenStream.noneMatch(
                                        item ->
                                                item.getValue() != null
                                                        && value.equals(
                                                        StringUtils.removeAllQuotes(item.getValue().getText()))))
                .orElse(true);
    }

    public InsertHandler<LookupElement> createInsertFieldHandler(final Field field) {
        return new YamlInsertFieldHandler(field);
    }

    public InsertHandler<LookupElement> createInsertValueHandler() {
        return new YamlInsertValueHandler();
    }

    public List<YAMLKeyValue> getParentProperties(final PsiElement element) {
        return toYamlKeyValue(element.getParent().getParent())
                .map(YAMLKeyValue::getParentMapping)
                .map(YAMLMapping::getKeyValues)
                .map(children -> children.stream().map(this::toYamlKeyValue))
                .map(
                        children ->
                                children
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(Collectors.toList()))
                .orElse(ImmutableList.of());
    }

    private Optional<YAMLKeyValue> toYamlKeyValue(final PsiElement psiElement) {
        return psiElement instanceof YAMLKeyValue
                ? Optional.of((YAMLKeyValue) psiElement)
                : Optional.empty();
    }
}
