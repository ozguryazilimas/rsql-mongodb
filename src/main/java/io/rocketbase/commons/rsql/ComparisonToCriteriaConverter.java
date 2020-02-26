package io.rocketbase.commons.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.rocketbase.commons.rsql.argconverters.EntityFieldTypeConverter;
import io.rocketbase.commons.rsql.argconverters.NoOpConverter;
import io.rocketbase.commons.rsql.argconverters.StringToQueryValueConverter;
import io.rocketbase.commons.rsql.structs.ConversionInfo;
import io.rocketbase.commons.rsql.utils.LazyUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.rocketbase.commons.rsql.RSQLOperators.*;

public class ComparisonToCriteriaConverter {

    private List<StringToQueryValueConverter> converters = new ArrayList<>();

    public ComparisonToCriteriaConverter(ConversionService conversionService, MongoMappingContext mappingContext) {
        converters.add(new EntityFieldTypeConverter(conversionService, mappingContext));
        converters.add(new NoOpConverter());
    }

    public Criteria asCriteria(ComparisonNode node, Class<?> targetEntityClass) {
        ComparisonOperator operator = node.getOperator();
        List<Object> arguments = mapArgumentsToAppropriateTypes(operator, node, targetEntityClass);
        return makeCriteria(node.getSelector(), operator, arguments);
    }

    private Criteria makeCriteria(String selector, ComparisonOperator operator, List<Object> arguments) {

        if (EQUAL.equals(operator)) {
            return Criteria.where(selector).is(getFirst(operator, arguments));

        } else if (NOT_EQUAL.equals(operator)) {
            return Criteria.where(selector).ne(getFirst(operator, arguments));

        } else if (GREATER_THAN.equals(operator)) {
            return Criteria.where(selector).gt(getFirst(operator, arguments));

        } else if (GREATER_THAN_OR_EQUAL.equals(operator)) {
            return Criteria.where(selector).gte(getFirst(operator, arguments));

        } else if (LESS_THAN.equals(operator)) {
            return Criteria.where(selector).lt(getFirst(operator, arguments));

        } else if (LESS_THAN_OR_EQUAL.equals(operator)) {
            return Criteria.where(selector).lte(getFirst(operator, arguments));

        } else if (IN.equals(operator)) {
            return Criteria.where(selector).in(arguments);

        } else if (NOT_IN.equals(operator)) {
            return Criteria.where(selector).nin(arguments);

        } else if (IS_NULL.equals(operator)) {
            return Criteria.where(selector).is(null);

        } else if (NOT_NULL.equals(operator)) {
            return Criteria.where(selector).not().is(null);

        } else if (LIKE.equals(operator)) {
            return Criteria.where(selector).regex(buildRegexPatterns(getFirst(operator, arguments)));

        } else if (NOT_LIKE.equals(operator)) {
            return Criteria.where(selector).not().regex(buildRegexPatterns(getFirst(operator, arguments)));

        } else if (IGNORE_CASE.equals(operator)) {
            return Criteria.where(selector).is(Pattern.compile(String.valueOf(getFirst(operator, arguments)), Pattern.CASE_INSENSITIVE));

        } else if (IGNORE_CASE_LIKE.equals(operator)) {
            return Criteria.where(selector).not().regex(buildRegexPatterns(getFirst(operator, arguments)), "i");

        } else if (IGNORE_CASE_NOT_LIKE.equals(operator)) {
            return Criteria.where(selector).not().is(Pattern.compile(String.valueOf(getFirst(operator, arguments)), Pattern.CASE_INSENSITIVE));

        } else if (BETWEEN.equals(operator)) {
            return Criteria.where(selector).gte(getFirst(operator, arguments)).lte(getSecond(operator, arguments));

        } else if (NOT_BETWEEN.equals(operator)) {
            return Criteria.where(selector).not().gte(getFirst(operator, arguments)).lte(getSecond(operator, arguments));

        }
        throw new UnsupportedOperationException("operator: " + operator.getSymbol() + " is not implemented");
    }

    private String buildRegexPatterns(Object value) {
        String text = String.valueOf(value);
        if (!text.contains(".*")) {
            text = ".*" + text + ".*";
        }
        return text;
    }

    private Object getFirst(ComparisonOperator operator, List<Object> arguments) {
        if (arguments != null && arguments.size() >= 1) {
            return arguments.get(0);
        } else {
            throw new UnsupportedOperationException("You cannot perform the query operation " + operator.getSymbol() + " with anything except a single value.");
        }
    }

    private Object getSecond(ComparisonOperator operator, List<Object> arguments) {
        if (arguments != null && arguments.size() >= 2) {
            return arguments.get(1);
        } else {
            throw new UnsupportedOperationException("You cannot perform the query operation " + operator.getSymbol() + " with anything except a single value.");
        }
    }

    private List<Object> mapArgumentsToAppropriateTypes(ComparisonOperator operator, ComparisonNode node, Class<?> targetEntityClass) {
        return node.getArguments().stream().map(arg -> convert(new ConversionInfo()
                .setArgument(arg)
                .setOperator(operator)
                .setPathToField(node.getSelector())
                .setTargetEntityClass(targetEntityClass))).collect(Collectors.toList());
    }


    private Object convert(ConversionInfo conversionInfo) {
        return LazyUtils.firstThatReturnsNonNull(converters.stream()
                .map(converter -> converter.convert(conversionInfo))
                .collect(Collectors.toList()));
    }

}

