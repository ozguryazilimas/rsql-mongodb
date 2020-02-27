package io.rocketbase.commons.rsql.visitor;

import cz.jirutka.rsql.parser.ast.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class SelectorSetVisitor implements RSQLVisitor<Set<String>, Set<String>> {

    @Override
    public Set<String> visit(AndNode node, Set<String> param) {
        param.addAll(getChildCriteria(node, param));
        return param;
    }

    @Override
    public Set<String> visit(OrNode node, Set<String> param) {
        param.addAll(getChildCriteria(node, param));
        return param;
    }

    @Override
    public Set<String> visit(ComparisonNode node, Set<String> param) {
        param.add(node.getSelector());
        return param;
    }


    private Set<String> getChildCriteria(LogicalNode node, Set<String> param) {
        Set<String> result = new LinkedHashSet<>();
        for (Node n : node.getChildren()) {
            result.addAll(visit(n, param));
        }
        return result;
    }

    private Set<String> visit(Node node, Set<String> param) {
        if (node instanceof AndNode) {
            return visit((AndNode) node, param);
        } else if (node instanceof OrNode) {
            return visit((OrNode) node, param);
        } else {
            return visit((ComparisonNode) node, param);
        }
    }
}
