package io.rocketbase.commons.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import org.springframework.data.mongodb.core.query.Criteria;

public class RsqlMongoAdapter {

    private ComparisonToCriteriaConverter converter;

    public RsqlMongoAdapter(ComparisonToCriteriaConverter converter) {
        this.converter = converter;
    }

    public Criteria getCriteria(String rsql, Class<?> targetEntityType) {
        return new RSQLParser(RSQLOperators.supportedOperators())
                .parse(rsql)
                .accept(new CriteriaBuildingVisitor(converter, targetEntityType));
    }

}
