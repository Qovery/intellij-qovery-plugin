package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;
import com.qovery.intellij.plugin.util.SetUtil;

import java.util.Optional;
import java.util.Set;

public class ContextualDatabaseVersion implements ContextualValueSet {

    private final String[] POSTGRES_VERSION = {"9.4", "9.5", "9.6", "10", "11"};
    private final String[] MYSQL_VERSION = {"5.5", "5.6", "5.7", "8.0"};

    @Override
    public Set<String> offers(QoveryCompletionHelper completionHelper) {
        Optional<String> type = completionHelper.getValueOfKeyFromParentMapping("type");
        if (type.isPresent()) {
            switch (type.get()) {
                case "postgresql":
                    return SetUtil.of(POSTGRES_VERSION);
                case "mysql":
                    return SetUtil.of(MYSQL_VERSION);
                case "mongodb":
                    return SetUtil.of();
            }
        }
        return SetUtil.of();
    }


}
