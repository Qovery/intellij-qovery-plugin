package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.Set;

public class ValueSet {

    private final String path;
    private Set<String> proposals;
    /**
     * This field is used for getting proposals from the completion context,
     * it is used for instance when the context need specific information
     * like database version.
     * <p>
     * You can't get the versions from nothing: you need the name of the database
     * to get all versions according to it.
     */
    private ContextualValueSet contextualValueProponent = null;

    public ValueSet(Set<String> proposals, String paths) {
        this.proposals = proposals;
        this.path = paths;
    }

    public ValueSet(ContextualValueSet contextualValueProponent, String path) {
        this.contextualValueProponent = contextualValueProponent;
        this.path = path;
    }

    public Set<String> getProposals() {
        return proposals;
    }

    public String getPath() {
        return path;
    }

    public boolean isContextual() {
        return contextualValueProponent != null;
    }

    public ValueSet fromContext(QoveryCompletionHelper qoveryCompletionHelper) {
        if (isContextual()) {
            return new ValueSet(contextualValueProponent.offers(qoveryCompletionHelper), path);
        }
        return null;
    }
}
