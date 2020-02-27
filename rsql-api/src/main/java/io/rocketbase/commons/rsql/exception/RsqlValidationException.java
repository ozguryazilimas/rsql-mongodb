package io.rocketbase.commons.rsql.exception;

import lombok.Getter;

import java.util.*;

@Getter
public class RsqlValidationException extends RuntimeException {

    private final boolean validSyntax;

    private Map<String, List<String>> selectors;

    public RsqlValidationException(boolean validSyntax) {
        this.validSyntax = validSyntax;
    }

    public RsqlValidationException(boolean validSyntax, Map<String, List<String>> selectors) {
        this.validSyntax = validSyntax;
        this.selectors = selectors;
    }

    public RsqlValidationException(boolean validSyntax, Collection<String> selectors, String message) {
        this.validSyntax = validSyntax;

        Map<String, List<String>> value = new LinkedHashMap<>();
        for (String s : selectors) {
            value.put(s, Arrays.asList(message));
        }
        this.selectors = value;
    }

    public RsqlValidationException withSelector(String selector, String message) {
        if (selectors == null) {
            selectors = new LinkedHashMap<>();
        }
        if (!selectors.containsKey(selector)) {
            selectors.put(selector, new ArrayList<>());
        }
        selectors.get(selector).add(message);
        return this;
    }
}
