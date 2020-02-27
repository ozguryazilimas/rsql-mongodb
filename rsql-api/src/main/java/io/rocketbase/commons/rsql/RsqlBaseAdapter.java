package io.rocketbase.commons.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import io.rocketbase.commons.rsql.exception.RsqlValidationException;
import io.rocketbase.commons.rsql.visitor.SelectorSetVisitor;

import java.util.*;

public abstract class RsqlBaseAdapter {

    public Set<String> getSelectors(String rsql) {
        try {
            return parseSelectors(rsql);
        } catch (RSQLParserException | IllegalArgumentException e) {
            return Collections.emptySet();
        }
    }

    public boolean isValid(String rsql, String... allowedProperties) {
        return isValid(rsql, Arrays.asList(allowedProperties));
    }

    public boolean isValid(String rsql, Collection<String> allowedCollection) {
        try {
            validate(rsql, allowedCollection);
            return true;
        } catch (RsqlValidationException e) {
            return false;
        }
    }

    public void validate(String rsql, String... allowedProperties) {
        validate(rsql, Arrays.asList(allowedProperties));
    }

    public void validate(String rsql, Collection<String> allowedCollection) {
        try {
            // validate rsql
            Set<String> selectors = parseSelectors(rsql);
            // empty collection don't need further checks of valid selectors
            if (allowedCollection == null || allowedCollection.isEmpty()) {
                return;
            }
            selectors.removeAll(allowedCollection);
            if (!selectors.isEmpty()) {
                throw new RsqlValidationException(true, selectors, "not allowed");
            }
        } catch (RSQLParserException | IllegalArgumentException e) {
            throw new RsqlValidationException(false);
        }
    }

    protected Set<String> parseSelectors(String rsql) {
        Node parsed = new RSQLParser(RSQLOperators.supportedOperators())
                .parse(rsql);
        Set<String> selectors = new LinkedHashSet<>();
        parsed.accept(new SelectorSetVisitor(), selectors);
        return selectors;
    }
}
