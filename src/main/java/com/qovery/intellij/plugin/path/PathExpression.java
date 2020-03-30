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

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PathExpression {

    private static final String ANY_KEY = "*";
    private static final String ANY_KEYS = "**";
    private static final String SEPARATOR = ".";

    private final String path;

    PathExpression(final String path) {
        this.path = path;
    }

    String getCurrentPath() {
        return splitPath()[0];
    }

    @NotNull
    private String[] splitPath() {
        return path.split("(?<!\\\\)\\.");
    }

    PathExpression afterFirst() {
        final String[] parts = splitPath();

        final String afterFirst = Arrays.stream(parts).skip(1).collect(Collectors.joining(SEPARATOR));

        return new PathExpression(afterFirst);
    }

    PathExpression beforeLast() {
        final String[] parts = splitPath();

        final String beforeLast =
                Arrays.stream(parts)
                        .limit(parts.length == 1 ? 1 : parts.length - 1)
                        .collect(Collectors.joining(SEPARATOR));

        return new PathExpression(beforeLast);
    }

    boolean isEmpty() {
        return path.isEmpty();
    }

    String last() {
        String[] paths = splitPath();
        return paths[paths.length - 1];
    }

    String secondLast() {
        String[] paths = splitPath();
        return paths[paths.length - 2];
    }

    boolean hasOnePath() {
        return splitPath().length == 1;
    }

    boolean isAnyKey() {
        return ANY_KEY.equals(last());
    }

    boolean isAnyKeys() {
        return ANY_KEYS.equals(last());
    }

    String getPath() {
        return path;
    }
}
