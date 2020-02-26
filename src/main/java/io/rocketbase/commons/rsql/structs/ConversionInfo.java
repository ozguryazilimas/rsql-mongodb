package io.rocketbase.commons.rsql.structs;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public class ConversionInfo {

    private String pathToField;
    private String argument;
    private Class<?> targetEntityClass;
    private ComparisonOperator operator;

    public String getPathToField() {
        return pathToField;
    }

    public ConversionInfo setPathToField(String pathToField) {
        this.pathToField = pathToField;
        return this;
    }

    public String getArgument() {
        return argument;
    }

    public ConversionInfo setArgument(String argument) {
        this.argument = argument;
        return this;
    }

    public Class<?> getTargetEntityClass() {
        return targetEntityClass;
    }

    public ConversionInfo setTargetEntityClass(Class<?> targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
        return this;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public ConversionInfo setOperator(ComparisonOperator operator) {
        this.operator = operator;
        return this;
    }

}
