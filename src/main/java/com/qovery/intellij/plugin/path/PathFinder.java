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

package com.qovery.intellij.plugin.path;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.qovery.intellij.plugin.path.PathExpressionUtil.unescape;

public class PathFinder {

    private static final String DUMMY_IDENTIFIER = "IntellijIdeaRulezzz";
    private static final String ROOT_PATH = "$";
    private static final PathExpression ROOT_PATH_EXPRESSION = new PathExpression(ROOT_PATH);

    /*
     * Finds named children, stopping the traversal if a child is a list. For example, given:
     * items:
     *   - item1: value1
     *   - item2: value2
     *
     * The method would return an empty list when called with path "$" and "items" PSI element.
     */
    public List<? extends PsiNamedElement> findDirectNamedChildren(
            final String path, final PsiElement psiElement) {
        Predicate<PsiElement> childFilter =
                child ->
                        child instanceof NavigatablePsiElement
                                && !(child instanceof JsonStringLiteral)
                                && !(child instanceof YAMLSequence)
                                && !(child instanceof JsonArray);

        return findChildrenByPathFrom(new PathExpression(path), psiElement, childFilter);
    }

    public boolean isInsidePath(final PsiElement psiElement, final String path) {
        return isInsidePath(psiElement, new PathExpression(path));
    }

    private boolean isInsidePath(final PsiElement psiElement, PathExpression pathExpression) {
        if (psiElement == null) {
            return false;
        }

        final PsiNamedElement nextNamedParent = getNextNamedParent(psiElement);
        final String unescapedTargetKeyName = unescape(pathExpression.last());

        if (pathExpression.isAnyKey()) {
            return isInsidePath(
                    getNextNamedParent(nextNamedParent.getParent()), pathExpression.beforeLast());
        }

        if (pathExpression.isAnyKeys()) {
            return isInsidePath(
                    goUpToElementWithParentName(psiElement, pathExpression.secondLast()),
                    pathExpression.beforeLast());
        }

        if (unescapedTargetKeyName.equals(ROOT_PATH)) {
            return nextNamedParent instanceof PsiFile;
        }

        return unescapedTargetKeyName.equals(nextNamedParent.getName())
                && (pathExpression.hasOnePath()
                || isInsidePath(nextNamedParent.getParent(), pathExpression.beforeLast()));
    }

    private boolean isRoot(final PsiElement psiElement) {
        return psiElement instanceof PsiFile;
    }

    private PsiNamedElement goUpToElementWithParentName(
            final PsiElement psiElement, final String keyName) {
        if (psiElement == null) {
            return null;
        }

        if (psiElement instanceof PsiNamedElement) {
            final PsiNamedElement psiNamedElement = (PsiNamedElement) psiElement;

            if (unescape(keyName).equals(psiNamedElement.getName())) {
                return (PsiNamedElement) psiElement;
            } else if (keyName.equals(ROOT_PATH)) {
                return isRoot(psiElement)
                        ? (PsiNamedElement) psiElement
                        : goUpToElementWithParentName(psiElement.getParent(), keyName);
            }
        }

        return goUpToElementWithParentName(psiElement.getParent(), keyName);
    }

    private PsiNamedElement getNextNamedParent(final PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }

        if (psiElement instanceof PsiNamedElement) {
            final PsiNamedElement namedElement = (PsiNamedElement) psiElement;

            if (namedElement.getName() != null && !namedElement.getName().contains(DUMMY_IDENTIFIER)) {
                return namedElement;
            }
        }

        return getNextNamedParent(psiElement.getParent());
    }

    private List<? extends PsiNamedElement> getNamedChildren(
            final PsiElement psiElement, Predicate<PsiElement> childFilter) {
        List<PsiNamedElement> children =
                Arrays.stream(psiElement.getChildren())
                        .filter(child -> child instanceof PsiNamedElement)
                        .map(child -> (PsiNamedElement) child)
                        .collect(Collectors.toList());

        if (children.isEmpty()) {
            Optional<PsiElement> navigatablePsiElement =
                    Arrays.stream(psiElement.getChildren()).filter(childFilter).findFirst();

            return navigatablePsiElement.isPresent()
                    ? getNamedChildren(navigatablePsiElement.get(), childFilter)
                    : new ArrayList<>();
        }

        return new ArrayList<>(children);
    }

    private Optional<PsiElement> findByPathFrom(
            final PathExpression pathExpression,
            final PsiElement psiElement,
            Predicate<PsiElement> childFilter) {
        if (pathExpression.isEmpty()) {
            return Optional.of(psiElement);
        }

        final String currentNodeName = pathExpression.getCurrentPath();
        final PathExpression remainingPathExpression = pathExpression.afterFirst();

        final Optional<? extends PsiElement> childByName =
                getChildByName(psiElement, currentNodeName, childFilter);

        return childByName.flatMap(el -> findByPathFrom(remainingPathExpression, el, childFilter));
    }

    private List<? extends PsiNamedElement> findChildrenByPathFrom(
            final PathExpression pathExpression,
            final PsiElement psiElement,
            Predicate<PsiElement> childFilter) {
        if (psiElement == null) {
            return new ArrayList<>();
        }

        if (pathExpression.isEmpty()) {
            return getNamedChildren(psiElement, childFilter);
        }

        final String currentNodeName = pathExpression.getCurrentPath();
        final PathExpression remainingPathExpression = pathExpression.afterFirst();

        if ("parent".equals(currentNodeName)) {
            return findChildrenByPathFrom(
                    ROOT_PATH_EXPRESSION, getNextObjectParent(psiElement), childFilter);
        }

        final Optional<? extends PsiElement> childByName =
                getChildByName(psiElement, currentNodeName, childFilter);

        return childByName
                .map(el -> findChildrenByPathFrom(remainingPathExpression, el, childFilter))
                .orElseGet(ArrayList::new);
    }

    private Optional<? extends PsiElement> getChildByName(
            final PsiElement psiElement, final String name, Predicate<PsiElement> childFilter) {
        if (ROOT_PATH.equals(name)) {
            return Optional.of(psiElement);
        }

        List<PsiNamedElement> children =
                Arrays.stream(psiElement.getChildren())
                        .filter(child -> child instanceof PsiNamedElement)
                        .map(child -> (PsiNamedElement) child)
                        .collect(Collectors.toList());

        if (children.isEmpty()) {
            Optional<PsiElement> navigatablePsiElement =
                    Arrays.stream(psiElement.getChildren())
                            .filter(child -> child instanceof NavigatablePsiElement)
                            .filter(child -> !(child instanceof JsonStringLiteral))
                            .findFirst();

            return navigatablePsiElement.isPresent()
                    ? getChildByName(navigatablePsiElement.get(), name, childFilter)
                    : Optional.empty();
        }

        final String unescapedName = unescape(name);

        return children.stream().filter(child -> unescapedName.equals(child.getName())).findFirst();
    }

    private PsiElement getNextObjectParent(final PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }

        if (psiElement instanceof JsonObject
                || (psiElement instanceof YAMLKeyValue || psiElement instanceof YAMLMapping)
                && !(psiElement instanceof JsonStringLiteral)) {
            return psiElement;
        }

        return getNextObjectParent(psiElement.getParent());
    }
}
